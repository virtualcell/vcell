#!/usr/bin/env bash

thrift -out ../vcell-core/src/main/java -v --gen java:generated_annotations=undated VisMesh.thrift
thrift -out ../pythonVtk -v --gen py VisMesh.thrift
