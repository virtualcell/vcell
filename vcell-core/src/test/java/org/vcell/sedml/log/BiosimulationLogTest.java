package org.vcell.sedml.log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("Fast")
public class BiosimulationLogTest {
    static String json_element_beginning = """
            {
              "status": "QUEUED",
              "exception": null,
              "skipReason": null,
              "output": null,
              "duration": null,
              "sedDocuments": [
                {
                  "location": "doc_1.sedml",
                  "status": "QUEUED",
                  "exception": null,
                  "skipReason": null,
                  "output": null,
                  "duration": null,
                  "tasks": [
                    {
                      "id": "task_1_ss",
                      "status": "QUEUED",
                      "exception": null,
                      "skipReason": null,
                      "output": null,
                      "duration": null,
                      "algorithm": null,
                      "simulatorDetails": null
                    },
                    {
                      "id": "task_2_time_course",
                      "status": "QUEUED",
                      "exception": null,
                      "skipReason": null,
                      "output": null,
                      "duration": null,
                      "algorithm": null,
                      "simulatorDetails": null
                    }
                  ],
                  "outputs": [
                    {
                      "id": "report_1",
                      "status": "QUEUED",
                      "exception": null,
                      "skipReason": null,
                      "output": null,
                      "duration": null,
                      "dataSets": [
                        {
                          "id": "dataset_1",
                          "status": "QUEUED"
                        },
                        {
                          "id": "dataset_2",
                          "status": "QUEUED"
                        }
                      ]
                    },
                    {
                      "id": "plot_1",
                      "status": "QUEUED",
                      "exception": null,
                      "skipReason": null,
                      "output": null,
                      "duration": null,
                      "curves": [
                        {
                          "id": "curve_1",
                          "status": "QUEUED"
                        }
                      ]
                    }
                  ]
                }
              ]
            }
            """;
    static String json_element_success = """
            {
              "status": "SUCCEEDED",
              "exception": null,
              "skipReason": null,
              "output": null,
              "duration": 6,
              "sedDocuments": [
                {
                  "location": "doc_1.sedml",
                  "status": "SUCCEEDED",
                  "exception": null,
                  "skipReason": null,
                  "output": null,
                  "duration": 5,
                  "tasks": [
                    {
                      "id": "task_1_ss",
                      "status": "SUCCEEDED",
                      "exception": null,
                      "skipReason": null,
                      "output": "Reading model ... done\\nInitializing simulation ... done\\nExecuting simulation ... done\\n",
                      "duration": 2,
                      "algorithm": null,
                      "simulatorDetails": null
                    },
                    {
                      "id": "task_2_time_course",
                      "status": "SUCCEEDED",
                      "exception": null,
                      "skipReason": null,
                      "output": "Reading model ... done\\nInitializing simulation ... done\\nExecuting simulation ... done\\n",
                      "duration": 1,
                      "algorithm": null,
                      "simulatorDetails": null
                    }
                  ],
                  "outputs": [
                    {
                      "id": "report_1",
                      "status": "SUCCEEDED",
                      "exception": null,
                      "skipReason": null,
                      "output": null,
                      "duration": 0.1,
                      "dataSets": [
                        {
                          "id": "dataset_1",
                          "status": "SUCCEEDED"
                        },
                        {
                          "id": "dataset_2",
                          "status": "SUCCEEDED"
                        }
                      ]
                    },
                    {
                      "id": "plot_1",
                      "status": "SUCCEEDED",
                      "exception": null,
                      "skipReason": null,
                      "output": null,
                      "duration": 0.01,
                      "curves": [
                        {
                          "id": "curve_1",
                          "status": "SUCCEEDED"
                        }
                      ]
                    }
                  ]
                }
              ]
            }
            """;
    static String json_element_failure = """
            {
              "status": "FAILED",
              "exception": null,
              "skipReason": null,
              "output": null,
              "duration": 6,
              "sedDocuments": [
                {
                  "location": "doc_1.sedml",
                  "status": "FAILED",
                  "exception": null,
                  "skipReason": null,
                  "output": null,
                  "duration": 5,
                  "tasks": [
                    {
                      "id": "task_1_ss",
                      "status": "SUCCEEDED",
                      "exception": null,
                      "skipReason": null,
                      "output": "Reading model ... done\\nInitializing simulation ... done\\nExecuting simulation ... done\\n",
                      "duration": 2,
                      "algorithm": null,
                      "simulatorDetails": null
                    },
                    {
                      "id": "task_2_time_course",
                      "status": "FAILED",
                      "exception": {
                        "type": "FileNotFoundError",
                        "message": "Model `model2.xml` does not exist."
                      },
                      "skipReason": null,
                      "output": null,
                      "duration": 1,
                      "algorithm": null,
                      "simulatorDetails": null
                    }
                  ],
                  "outputs": [
                    {
                      "id": "report_1",
                      "status": "SUCCEEDED",
                      "exception": null,
                      "skipReason": null,
                      "output": null,
                      "duration": 0.1,
                      "dataSets": [
                        {
                          "id": "dataset_1",
                          "status": "SUCCEEDED"
                        },
                        {
                          "id": "dataset_2",
                          "status": "SUCCEEDED"
                        }
                      ]
                    },
                    {
                      "id": "plot_1",
                      "status": "SKIPPED",
                      "exception": null,
                      "skipReason": {
                        "type": "2DPlotNotImplemented",
                        "message": "Output skipped because the simulator cannot generate plots."
                      },
                      "output": null,
                      "duration": 0.01,
                      "curves": [
                        {
                          "id": "curve_1",
                          "status": "SKIPPED"
                        }
                      ]
                    }
                  ]
                }
              ]
            }
            """;
    static String json_document_failed = """
            {
              "status": "FAILED",
              "exception": null,
              "skipReason": null,
              "output": null,
              "duration": 6,
              "sedDocuments": [
                {
                  "location": "doc_1.sedml",
                  "status": "FAILED",
                  "exception": {
                    "type": "FileNotFoundError",
                    "message": "Model `model2.xml` does not exist."
                  },
                  "skipReason": null,
                  "output": "Reading model ... done\\nInitializing simulation ... done\\nExecuting simulation ... done\\n",
                  "duration": 5,
                  "tasks": null,
                  "outputs": null
                }
              ]
            }
            """;

    public static Collection<String> testCases() {
        return List.of(json_element_beginning, json_element_success, json_element_failure, json_document_failed);
    }

    @ParameterizedTest
    @MethodSource("testCases")
    public void testArchiveLogRead(String expectedJson) throws IOException {
        // create ArchiveLog from given JSON
        BiosimulationLog.ArchiveLog archiveLog = BiosimulationLog.ArchiveLog.fromJson(expectedJson);

        // generate new JSON string from ArchiveLog
        String newJson = archiveLog.writeToJson();

        // compare the original JSON string with the new JSON string - normalize by pretty printing
        assertEquals(prettyPrintJson(expectedJson), prettyPrintJson(newJson));
    }


    public static String prettyPrintJson(String jsonString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonString);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return objectMapper.writeValueAsString(jsonNode);
    }

}
