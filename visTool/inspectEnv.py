import argparse
import sys
import subprocess
import traceback
from __builtin__ import isinstance

def main():

    print(" python search path " + str(sys.path));

    cmd = 'C:/Users/vasilescu/.vcell/Miniconda/Scripts/conda remove -y -v python-copasi'
    p = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out, err = p.communicate()
    print out
    #print err

    cmd = 'C:/Users/vasilescu/.vcell/Miniconda/Scripts/conda remove -y -v vtk'
    p = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out, err = p.communicate()
    print out
    #print err

    cmd = 'C:/Users/vasilescu/.vcell/Miniconda/Scripts/conda remove -y -v thrift'
    p = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out, err = p.communicate()
    print out
    #print err

    cmd = 'C:/Users/vasilescu/.vcell/Miniconda/Scripts/conda remove -y -v python-libsbml'
    p = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out, err = p.communicate()
    print out
    #print err

    # -----------------------------------------

    cmd = 'C:/Users/vasilescu/.vcell/Miniconda/Scripts/conda install -y -c fbergmann python-copasi'
    p = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out, err = p.communicate()
    print out
    print err

    cmd = 'C:/Users/vasilescu/.vcell/Miniconda/Scripts/conda install -y -c conda-forge vtk'
    p = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out, err = p.communicate()
    print out
    print err

    cmd = 'C:/Users/vasilescu/.vcell/Miniconda/Scripts/conda install -y -c conda-forge thrift'
    p = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out, err = p.communicate()
    print out
    print err

    cmd = 'C:/Users/vasilescu/.vcell/Miniconda/Scripts/conda install -y -c sbmlteam python-libsbml'
    p = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out, err = p.communicate()
    print out
    print err

    return 0

'''
    except:
        e_info = sys.exc_info()
        traceback.print_exception(e_info[0],e_info[1],e_info[2],file=sys.stdout)
        sys.stderr.write("exception: " + "\n")
        sys.stderr.flush()
        return -1
'''

if __name__ == '__main__':
    main()
