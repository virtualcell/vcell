import json
import os
import stat
import sys
import tempfile
from typing import List

import biosimulators_utils.sedml.utils
import fire
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import seaborn as sns
from biosimulators_utils.combine.data_model import CombineArchive
from biosimulators_utils.combine.io import CombineArchiveReader
from biosimulators_utils.combine.utils import get_sedml_contents
from biosimulators_utils.config import Config
from biosimulators_utils.log.data_model import Status, CombineArchiveLog, SedDocumentLog  # noqa: F401
from biosimulators_utils.combine.validation import validate
from biosimulators_utils.combine.data_model import CombineArchiveContentFormat

from biosimulators_utils.report.data_model import DataSetResults, ReportResults, ReportFormat  # noqa: F401
from biosimulators_utils.sedml.data_model import Report, Plot2D, Plot3D, DataSet, SedDocument
from biosimulators_utils.sedml.io import SedmlSimulationReader, SedmlSimulationWriter

# Move status PY code here
# Create temp directory
tmp_dir = tempfile.mkdtemp()

def validate_omex(omex_file_path: str, temp_dir_path: str, omex_json_report_path: str) -> str:
    if not os.path.exists(temp_dir_path):
        os.mkdir(temp_dir_path, stat.S_IRWXU | stat.S_IRWXG | stat.S_IRWXO)

    # defining archive
    config = Config(
        VALIDATE_OMEX_MANIFESTS=True,
        VALIDATE_SEDML=True,
        VALIDATE_SEDML_MODELS=True,
        VALIDATE_IMPORTED_MODEL_FILES=True,
        VALIDATE_OMEX_METADATA=True,
        VALIDATE_IMAGES=True,
        VALIDATE_RESULTS=True
    )

    reader = CombineArchiveReader()
    archive: CombineArchive = reader.run(in_file=omex_file_path, out_dir=temp_dir_path, config=config)
    print("errors: "+str(reader.errors)+"\n"+"warnings: "+str(reader.warnings))

    validator_errors: List[str] = []
    validator_warnings: List[str] = []
    if len(reader.errors) == 0:
        validator_errors, validator_warnings = validate(
            archive,
            temp_dir_path,
            formats_to_validate=list(CombineArchiveContentFormat.__members__.values()),
            config=config
        )

    results_dict = {
                "parse_errors": reader.errors,
                "parse_warnings": reader.warnings,
                "validator_errors": validator_errors,
                "validator_warnings": validator_warnings
            }
    with open(omex_json_report_path, "w") as file:
        file.write(json.dumps(results_dict, indent=2))
    return repr(results_dict)


def gen_sedml_2d_3d(omex_file_path, base_out_path) -> str:

    temp_path = os.path.join(base_out_path, "temp")
    if not os.path.exists(temp_path):
        os.mkdir(temp_path, stat.S_IRWXU | stat.S_IRWXG | stat.S_IRWXO)

    # defining archive
    config = Config(VALIDATE_OMEX_MANIFESTS=True)
    archive = CombineArchiveReader().run(in_file=omex_file_path, out_dir=temp_path,
                                         config=config)

    # determine files to execute
    sedml_contents = get_sedml_contents(archive)

    for i_content, content in enumerate(sedml_contents):
        content_filename = os.path.normpath(os.path.join(temp_path, content.location))
        starting_sedml_name_index = content.location.rfind("/") + 1 if '/' in content.location else 0
        ending_sedml_name_index = content.location.rfind(".")
        sedml_name = content.location[starting_sedml_name_index:ending_sedml_name_index]
#        sedml_name = Path(content.location).stem
        print("name: ", sedml_name, file=sys.stdout)
        print("sedml_name: ", sedml_name, file=sys.stdout)
        print("content.location: ", content.location, file=sys.stdout)
        print("content_filename: ", content_filename, file=sys.stdout)

        doc = SedmlSimulationReader().run(content_filename)
        for output in doc.outputs:
            if isinstance(output, (Plot2D, Plot3D)):
                report = Report(
                    id='__plot__' + output.id,
                    name=output.name)

                data_generators = {}
                if isinstance(output, Plot2D):
                    for curve in output.curves:
                        data_generators[curve.x_data_generator.id] = curve.x_data_generator
                        data_generators[curve.y_data_generator.id] = curve.y_data_generator

                elif isinstance(output, Plot3D):
                    for surface in output.surfaces:
                        data_generators[surface.x_data_generator.id] = surface.x_data_generator
                        data_generators[surface.y_data_generator.id] = surface.y_data_generator
                        data_generators[surface.z_data_generator.id] = surface.z_data_generator

                for data_generator in data_generators.values():
                    report.data_sets.append(DataSet(
                        id='__vcell_reserved_data_set_prefix__{}_{}'.format(
                            output.id, data_generator.id),
                        name=data_generator.name,
                        label=data_generator.id,
                        data_generator=data_generator,
                    ))

                report.data_sets.sort(key=lambda data_set: data_set.id)
                doc.outputs.append(report)

        filename_with_reports_for_plots = os.path.join(
            temp_path, f'simulation_{sedml_name}.sedml')
        SedmlSimulationWriter().run(doc, filename_with_reports_for_plots,
                                    validate_models_with_languages=False)
        if not os.path.exists(filename_with_reports_for_plots):
            raise FileNotFoundError("The desired pseudo-sedml failed to generate!")
    return temp_path


def transpose_vcml_csv(csv_file_path: str) -> str:
    df = pd.read_csv(csv_file_path, header=None)
    cols = list(df.columns)
    final_cols = [col for col in cols if col != '']
    df[final_cols].transpose().to_csv(csv_file_path, header=False, index=False)
    print("Success!")
    return csv_file_path


def get_all_dataref_and_curves(sedml_path):
    all_plot_curves = {}
    all_report_dataref = {}

    sedml: SedDocument = SedmlSimulationReader().run(sedml_path)

    for output in sedml.outputs:
        if isinstance(output, Plot2D):
            all_curves = {}
            for curve in output.curves:
                all_curves[curve.getId()] = {
                    'x': curve.getXDataReference(),
                    'y': curve.getYDataReference()
                }
            all_plot_curves[output.id] = all_curves
        if isinstance(output, Report):
            for dataset in output.data_sets():

                ######
                if output.id in all_report_dataref:
                    all_report_dataref[output.id].append({
                        'data_reference': dataset.getDataReference(),
                        'data_label': dataset.getLabel()
                    })
                else:
                    all_report_dataref[output.id] = []
                    all_report_dataref[output.id].append({
                        'data_reference': dataset.getDataReference(),
                        'data_label': dataset.getLabel()
                    })

    return all_report_dataref, all_plot_curves


def get_report_label_from_data_ref(dataref: str, all_report_dataref):
    for report in all_report_dataref.keys():
        for data_ref in all_report_dataref[report]:
            if dataref == data_ref['data_reference']:
                return report, data_ref['data_label']




# Update plots dict

def update_dataref_with_report_label(all_report_dataref, all_plot_curves):

    for plot, curves in all_plot_curves.items():
        for curve_name, datarefs in curves.items():
            new_ref = dict(datarefs)
            new_ref['x'] = get_report_label_from_data_ref(
                datarefs['x'], all_report_dataref)[1]
            new_ref['y'] = get_report_label_from_data_ref(
                datarefs['y'], all_report_dataref)[1]
            new_ref['report'] = get_report_label_from_data_ref(
                datarefs['y'], all_report_dataref)[0]
            curves[curve_name] = new_ref

    return all_report_dataref, all_plot_curves


def get_report_dataframes(all_report_dataref, result_out_dir):
    report_frames = {}
    reports_list = list(set(all_report_dataref.keys()))
    for report in reports_list:
        report_frames[report] = pd.read_csv(str(os.path.join(result_out_dir, report + ".csv"))).T.reset_index()
        report_frames[report].columns = report_frames[report].iloc[0].values
        report_frames[report].drop(index=0, inplace=True)
    return report_frames

# PLOTTING


def plot_and_save_curves(all_plot_curves, report_frames, result_out_dir) -> str:
    all_plots = dict(all_plot_curves)
    for plot, curve_dat in all_plots.items():
        dims = (12, 8)
        fig, ax = plt.subplots(figsize=dims)
        for curve, data in curve_dat.items():
            df = report_frames[data['report']]
            df.to_csv(os.path.join(result_out_dir, plot + '.csv'),
                      index=False, header=True)
            transpose_vcml_csv(os.path.join(result_out_dir, plot + '.csv'))
            sns.lineplot(x=df[data['x']].astype(
                np.float64), y=df[data['y']].astype(np.float64), ax=ax, label=curve)
            ax.set_ylabel('')
        #         plt.show()
        plt.savefig(os.path.join(result_out_dir, plot + '.pdf'), dpi=300)
        return os.path.join(result_out_dir, plot + '.pdf')


def gen_plot_pdfs(sedml_path, result_out_dir):
    all_report_dataref, all_plot_curves = get_all_dataref_and_curves(
        sedml_path)
    all_report_dataref, all_plot_curves = update_dataref_with_report_label(
        all_report_dataref, all_plot_curves)
    report_frames = get_report_dataframes(all_report_dataref, result_out_dir)
    print("Success!")
    return plot_and_save_curves(all_plot_curves, report_frames, result_out_dir)


def gen_plots_for_sed2d_only(sedml_path, result_out_dir):
    sedml: SedDocument = SedmlSimulationReader().run(sedml_path)

    # Generate all_plot_curves
    all_plot_curves = {}
    for output in sedml.outputs:
        if not isinstance(output, Plot2D):
            continue
        sed_plot_2d: Plot2D = output
        all_curves = {}

        for curve in sed_plot_2d.curves:
            all_curves[curve.id] = {
                'x': curve.x_data_generator,
                'y': curve.y_data_generator,
                'name': curve.name
            }
        all_plot_curves[sed_plot_2d.id] = all_curves


    all_plots = dict(all_plot_curves)
    for plot_id, curve_dat_dict in all_plots.items(): # curve_dat_dict <--> all_curves
        dims = (12, 8)
        fig, ax = plt.subplots(figsize=dims)

        labelMap = {}
        df = pd.read_csv(os.path.join(result_out_dir, plot_id + '.csv'), header=None).T
        
        # create mapping from task to all repeated tasks (or just itself)
        for elem in df.iloc[1]:
            if elem not in labelMap:
                labelMap[elem] = []
            labelMap[elem].append(str(elem) + "_" + str(len(labelMap[elem])))

        # Prepare new labels
        labels = []
        for key in labelMap:
            if len(labelMap[key]) == 1:
                labelMap[key][0] = key      # If there wasn't repeated tasks, restore the old name
            for elem in labelMap[key]:
                labels.append(elem)

        # format data frame
        df.columns = labels
        labels_df = df.copy()

        df.drop(df.index[:3], inplace=True)
        labels_df.drop(labels_df.index[:2], inplace=True)
        labels_df.drop(labels_df.index[1:], inplace=True)

        df.reset_index(inplace=True)
        df.drop('index', axis=1, inplace=True)

        for curve_id, data in curve_dat_dict.items(): # data <--> (dict)all_curves.values()
            shouldLabel = True
            x_axis_id = data['x'].id
            y_axis_id = data['y'].id
            x_data_sets = labelMap[x_axis_id]
            y_data_sets = labelMap[y_axis_id]

            for i in range(len(y_data_sets)):
                series_name = y_data_sets[i]
                x_data_set = x_data_sets[0] if len(x_data_sets) == 1 else x_data_sets[i]
                label_name = data['name'] if data['name'] is not None else curve_id
                sns.lineplot(data=df, x=x_data_set, y=series_name, ax=ax, label=(label_name if shouldLabel else None))
                ax.set_ylabel('')
                ax.set_xlabel(labels_df.at[labels_df.index[0], x_data_set])
                shouldLabel = False
            plt.savefig(os.path.join(result_out_dir, plot_id + '.pdf'), dpi=300)
    print("Success!")
    return result_out_dir


if __name__ == "__main__":
    fire.Fire({
        'genSedml2d3d': gen_sedml_2d_3d,
        'genPlotsPseudoSedml': gen_plots_for_sed2d_only,
        'transposeVcmlCsv': transpose_vcml_csv,
        'genPlotPdfs': gen_plot_pdfs,
    })
