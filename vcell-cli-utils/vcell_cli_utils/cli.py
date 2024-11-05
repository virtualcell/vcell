import glob
import json
import os
import shutil
import stat
import sys
import tempfile
from dataclasses import dataclass
from typing import List

import biosimulators_utils.sedml.utils
import fire
import libsedml as lsed
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

# from biosimulators_utils.plot.data_model import PlotFormat  # noqa: F401
from biosimulators_utils.report.data_model import DataSetResults, ReportResults, ReportFormat  # noqa: F401
from biosimulators_utils.report.io import ReportWriter
from biosimulators_utils.sedml.data_model import Report, Plot2D, Plot3D, DataSet
from biosimulators_utils.sedml.io import SedmlSimulationReader, SedmlSimulationWriter
from deprecated import deprecated
from libsedml import SedReport, SedPlot2D, SedDocument

# Move status PY code here
# Create temp directory
tmp_dir = tempfile.mkdtemp()


def validate_omex(omex_file_path: str, temp_dir_path: str, omex_json_report_path: str) -> None:
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

    with open(omex_json_report_path, "w") as file:
        file.write(json.dumps(
            {
                "parse_errors": reader.errors,
                "parse_warnings": reader.warnings,
                "validator_errors": validator_errors,
                "validator_warnings": validator_warnings
            }, indent=2))


def gen_sedml_2d_3d(omex_file_path, base_out_path):

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
        content_filename = os.path.join(temp_path, content.location)
        starting_sedml_name_index = content.location.rfind("/") if '/' in content.location else 0
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
    return temp_path


@deprecated("This method is no longer used")
def exec_sed_doc(omex_file_path, base_out_path):
    # defining archive
    config = Config(VALIDATE_OMEX_MANIFESTS=False)
    archive = CombineArchiveReader().run(in_file=omex_file_path, out_dir=tmp_dir,
                                         config=config)

    # determine files to execute
    sedml_contents = get_sedml_contents(archive)

    report_results = ReportResults()
    for i_content, content in enumerate(sedml_contents):
        content_filename = os.path.join(tmp_dir, content.location)

        doc = SedmlSimulationReader().run(content_filename)

        for report_filename in glob.glob(os.path.join(base_out_path, content.location, '*.csv')):
            report_id = os.path.splitext(os.path.basename(report_filename))[0]

            # read report from CSV file produced by VCell
            data_set_df = pd.read_csv(report_filename).transpose()
            data_set_df.columns = data_set_df.iloc[0]
            data_set_df = data_set_df.drop(data_set_df.iloc[0].name)
            data_set_df = data_set_df.reset_index()
            data_set_df = data_set_df.rename(
                columns={'index': data_set_df.columns.name})
            data_set_df = data_set_df.transpose()
            data_set_df.index.name = None

            report = next(
                report for report in doc.outputs if report.id == report_id)

            data_set_results = DataSetResults()

            # print("report: ", report, file=sys.stderr)
            # print("report Type: ", type(report), file=sys.stderr)
            # print("Plot Type: ", Plot2D, file=sys.stderr)
            if type(report) != Plot2D and type(report) != Plot3D:
                # Considering the scenario where it has the datasets in sedml
                for data_set in report.data_sets:
                    data_set_results[data_set.id] = data_set_df.loc[data_set.label, :].to_numpy(
                        dtype='float64')
                    # print("DF for report: ", data_set_results[data_set.id], file=sys.stderr)
                    # print("df.types: ", data_set_results[data_set.id].dtype, file=sys.stderr)
            else:
                data_set_df = pd.read_csv(report_filename, header=None).T
                data_set_df.columns = data_set_df.iloc[0]
                data_set_df.drop(0, inplace=True)
                data_set_df.reset_index(inplace=True)
                data_set_df.drop('index', axis=1, inplace=True)
                # print("DF for plot: ", data_set_df, file=sys.stderr)
                # Considering the scenario where it doesn't have datasets in sedml (pseudo sedml for plots)
                for col in list(data_set_df.columns):
                    data_set_results[col] = data_set_df[col].values

            # append to data structure of report results
            report_results[report_id] = data_set_results

            # save file in desired BioSimulators format(s)
            # for report_format in report_formats:
            # print("HDF report: ", report, file=sys.stderr)
            # print("HDF dataset results: ", data_set_results, file=sys.stderr)
            # print("HDF base_out_path: ", base_out_path,file=sys.stderr)
            # print("HDF path: ", os.path.join(content.location, report.id), file=sys.stderr)

            rel_path = os.path.join(content.location, report.id)

            if len(rel_path.split("./")) > 1:
                rel_path = rel_path.split("./")[1]

            if type(report) != Plot2D and type(report) != Plot3D:
                ReportWriter().run(report,
                                   data_set_results,
                                   base_out_path,
                                   rel_path,
                                   format='h5')
            else:
                datasets = []
                for col in list(data_set_df.columns):
                    datasets.append(DataSet(id=col, label=col, name=col))
                report.data_sets = datasets
                ReportWriter().run(report,
                                   data_set_results,
                                   base_out_path,
                                   rel_path,
                                   format='h5')

    # Remove temp directory
    shutil.rmtree(tmp_dir)


def transpose_vcml_csv(csv_file_path: str):
    df = pd.read_csv(csv_file_path, header=None)
    cols = list(df.columns)
    final_cols = [col for col in cols if col != '']
    df[final_cols].transpose().to_csv(csv_file_path, header=False, index=False)


def get_all_dataref_and_curves(sedml_path):
    all_plot_curves = {}
    all_report_dataref = {}

    sedml = lsed.readSedML(sedml_path)

    for output in sedml.getListOfOutputs():
        if type(output) == SedPlot2D:
            all_curves = {}
            for curve in output.getListOfCurves():
                all_curves[curve.getId()] = {
                    'x': curve.getXDataReference(),
                    'y': curve.getYDataReference()
                }
            all_plot_curves[output.getId()] = all_curves
        if type(output) == SedReport:
            for dataset in output.getListOfDataSets():

                ######
                if output.getId() in all_report_dataref:

                    all_report_dataref[output.getId()].append({
                        'data_reference': dataset.getDataReference(),
                        'data_label': dataset.getLabel()
                    })
                else:
                    all_report_dataref[output.getId()] = []
                    all_report_dataref[output.getId()].append({
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
        report_frames[report] = pd.read_csv(os.path.join(
            result_out_dir, report + ".csv")).T.reset_index()
        report_frames[report].columns = report_frames[report].iloc[0].values
        report_frames[report].drop(index=0, inplace=True)
    return report_frames

# PLOTTING


def plot_and_save_curves(all_plot_curves, report_frames, result_out_dir):
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


def gen_plot_pdfs(sedml_path, result_out_dir):
    all_report_dataref, all_plot_curves = get_all_dataref_and_curves(
        sedml_path)
    all_report_dataref, all_plot_curves = update_dataref_with_report_label(
        all_report_dataref, all_plot_curves)
    report_frames = get_report_dataframes(all_report_dataref, result_out_dir)
    plot_and_save_curves(all_plot_curves, report_frames, result_out_dir)


def gen_plots_for_sed2d_only_2(sedml_path, result_out_dir):
    sedml: biosimulators_utils.sedml.data_model.SedDocument \
        = biosimulators_utils.sedml.io.SedmlSimulationReader().run(sedml_path)

    for plot in [output for output in sedml.outputs if isinstance(output, Plot2D)]:
        dims = (12, 8)
        _, ax = plt.subplots(figsize=dims)


        df = pd.read_csv(os.path.join(result_out_dir, plot.id + '.csv'), header=None).T

        # create mapping from task to all repeated tasks (or just itself)
        curve_id_mapping = {}
        for elem in df.iloc[1]:
            if elem not in curve_id_mapping:
                curve_id_mapping[elem] = []
            curve_id_mapping[elem].append(str(elem) + "_" + str(len(curve_id_mapping[elem])))

        labels = []
        for key in curve_id_mapping:
            if len(curve_id_mapping[key]) == 1:
                curve_id_mapping[key][0] = key  # If there wasn't repeated tasks, restore the old name
            for elem in curve_id_mapping[key]:
                labels.append(elem)

        # format data frame
        df.columns = labels
        df.drop(0, inplace=True)
        df.drop(1, inplace=True)
        df.drop(2, inplace=True)
        df.reset_index(inplace=True)
        df.drop('index', axis=1, inplace=True)

        with open("/home/ldrescher/DataFrameFile.df", "w+") as debug_file:
            debug_file.write(repr(df))

        for curve in plot.curves:
            should_label = True
            for curve_name in curve_id_mapping[curve.y_data_generator.id]:
                if curve.x_data_generator.id in labels:
                    raise(f"Can not find x data set `{curve.x_data_generator.id}` in data frame (legal set: {labels})")
                if curve_name not in labels:
                    raise(f"Can not find y data set `{curve_name}` in data frame (legal set: {labels})")
                sns.lineplot(data=df, x=curve.x_data_generator.id, y=curve_name, ax=ax,
                             label=(curve.id if should_label else None))
                ax.set_ylabel('')
                should_label = False
            plt.savefig(os.path.join(result_out_dir, plot.id + '.pdf'), dpi=300)

def gen_plots_for_sed2d_only(sedml_path, result_out_dir):
    all_plot_curves = {}

    sedml: SedDocument = lsed.readSedML(sedml_path)

    # Generate all_plot_curves
    for output in sedml.getListOfOutputs():
        if not isinstance(output, SedPlot2D):
            continue
        sed_plot_2d: SedPlot2D = output
        all_curves = {}
        all_plot_curves = {}


        for curve in sed_plot_2d.getListOfCurves():
            all_curves[curve.getId()] = {
                'x': curve.getXDataReference(),
                'y': curve.getYDataReference()
            }
        all_plot_curves[sed_plot_2d.getId()] = all_curves


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
                labelMap[key][0] = key      # If there wasn't repreated tasks, restore the old name
            for elem in labelMap[key]:
                labels.append(elem)

        # format data frame
        df.columns = labels 
        df.drop(0, inplace=True)
        df.drop(1, inplace=True)
        df.drop(2, inplace=True)
        df.reset_index(inplace=True)
        df.drop('index', axis=1, inplace=True)

        for curve_id, data in curve_dat_dict.items(): # data <--> (dict)all_curves.values()
            shouldLabel = True
            for series_name in labelMap[data['y']]:
                sns.lineplot(data=df, x=data['x'], y=series_name, ax=ax, label=(curve_id if shouldLabel else None))
                ax.set_ylabel('')
                shouldLabel = False
            plt.savefig(os.path.join(result_out_dir, plot_id + '.pdf'), dpi=300)


if __name__ == "__main__":
    fire.Fire({
        'genSedml2d3d': gen_sedml_2d_3d,
        'genPlotsPseudoSedml': gen_plots_for_sed2d_only,
        'execSedDoc': exec_sed_doc,
        'transposeVcmlCsv': transpose_vcml_csv,
        'genPlotPdfs': gen_plot_pdfs,
    })
