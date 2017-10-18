import argparse
import sys
import traceback
from __builtin__ import isinstance

import vcellopt.ttypes as VCELLOPT
import optServiceCOPASI


def main():
    from thrift.TSerialization import TTransport
    from thrift.TSerialization import TBinaryProtocol
    from thrift.TSerialization import deserialize
    try:
        # --------------------------------------------------------------------------
        # parse input and deserialize the (thrift) optimization problem
        # --------------------------------------------------------------------------
        parser = argparse.ArgumentParser()
        parser.add_argument("optfile", help="filename of input optimization file")
        parser.add_argument("resultfile", help="filename of optimization results")
        args = parser.parse_args()
        assert isinstance(args.resultfile, str)
        result_file = args.resultfile

        f_optfile = open(args.optfile, "rb")
        blob_opt = f_optfile.read()
        print("read " + str(len(blob_opt)) + " bytes from " + args.optfile)
        f_optfile.close()

        vcell_opt_problem = VCELLOPT.OptProblem()
        protocol_factory = TBinaryProtocol.TBinaryProtocolFactory
        deserialize(vcell_opt_problem, blob_opt, protocol_factory=protocol_factory())
        print("done with deserialization")

        opt_run = optServiceCOPASI.process(vcell_opt_problem)
        assert(isinstance(opt_run, VCELLOPT.OptRun))

        transport_out = TTransport.TMemoryBuffer()
        protocol_out = TBinaryProtocol.TBinaryProtocol(transport_out)
        opt_run.write(protocol_out)
        with open(result_file, 'wb') as foutput:
            foutput.write(transport_out.getvalue())
            foutput.close()

    except:
        e_info = sys.exc_info()
        traceback.print_exception(e_info[0], e_info[1], e_info[2], file=sys.stdout)
        sys.stderr.write("exception: " + str(e_info[0]) + ": " + str(e_info[1]) + "\n")
        sys.stderr.flush()
        return -1
    else:
        return 0
    finally:
        pass


if __name__ == '__main__':
    main()
