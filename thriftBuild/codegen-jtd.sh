#!/usr/bin/env bash

jtd-codegen vcellopt.jtd.json --python-out ../pythonProject/vcell-opt/vcell_opt/data
jtd-codegen vcellopt.jtd.json --java-jackson-out ../vcell-core/src/main/java/org/vcell/optimization/jtd --java-jackson-package org.vcell.optimization.jtd