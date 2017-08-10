import vcellopt.ttypes as VCELLOPT

from thrift.TSerialization import TBinaryProtocol
from thrift.TSerialization import deserialize

import argparse
import sys
import subprocess
import traceback
from __builtin__ import isinstance

def main():
    try:
        import COPASI
        print "COPASI imported"
        #return 0
    except:
        print "COPASI didn't import"
        #return -1


    try:
        import vtk

        print "vtk imported"
        #return 0
    except:
        print "vtk didn't import"
        #return -1


    try:
        import thrift

        print "thrift imported"
        # return 0
    except:
        print "thrift didn't import"
        # return -1


    try:
        import libsbml          # to install use: conda install -c SBMLTeam python-libs

        print "SBML imported"
        return 0
    except:
        print "SBML didn't import"
        return -1


def main1():
    try:
        if _user_has_conda() == True:
            return 0
        else:
            return -1

    except:
        e_info = sys.exc_info()
        traceback.print_exception(e_info[0],e_info[1],e_info[2],file=sys.stdout)
        sys.stderr.write("exception: " + "\n")
        sys.stderr.flush()
        return -1
    else:
        return 0

def _user_has_conda():
    #cmd = 'conda --help'
    #cmd = 'conda info'
    #cmd = 'conda list'     # paste this to find what is installed
    cmd = 'python --version'
    #p = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    p = subprocess.Popen(cmd, shell=False, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out, err = p.communicate()
    if len(err) > 0 and len(out) == 0:
        return True
    else:
        return False

if __name__ == '__main__':
    main()
