import argparse
import sys
import traceback
from __builtin__ import isinstance

import vcellopt.ttypes as VCELLOPT
import optServiceCOPASI
import time
import sys

from stompest.config import StompConfig
from stompest.protocol import StompSpec
from stompest.sync import Stomp

'''
add to activemq.xml
<connector>
    <serverTransport uri="stomp://localhost:61613"/>
</connector>
'''


def main():
    try:
        # --------------------------------------------------------------------------
        # parse input and deserialize the (thrift) optimization problem
        # --------------------------------------------------------------------------
        parser = argparse.ArgumentParser()
        parser.add_argument("hostname", help="ActiveMQ host")
        parser.add_argument("port", help="ActiveMQ STOMP port")
        parser.add_argument("username", help="ActiveMQ username")
        parser.add_argument("password", help="ActiveMQ password")
        parser.add_argument("vhost", help="virtual host")

        args = parser.parse_args()
        assert isinstance(args.hostname, str)
        assert isinstance(args.port, str)
        assert isinstance(args.username, str)
        assert isinstance(args.password, str)
        assert isinstance(args.vhost, str)



        client = Stomp(StompConfig("tcp://" + args.hostname + ":" + args.port,
                                   login=args.username,
                                   passcode=args.password,
                                   version="1.2"))
        client.connect(versions=["1.2"], host = args.vhost)

        subscription = client.subscribe("/queue/optReqTest",
                                {StompSpec.ACK_HEADER: StompSpec.ACK_AUTO,
                                 StompSpec.ID_HEADER: '0'})

        while True:
            frame = client.receiveFrame()
            print frame.body


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
                sys.stderr.write("Optimization Exception: " + str(e_info[0]) + ": " + str(e_info[1]) + "\n")
                sys.stderr.flush()
            else:
                pass

    except:
        e_info = sys.exc_info()
        traceback.print_exception(e_info[0], e_info[1], e_info[2], file=sys.stdout)
        sys.stderr.write("Messaging Exception: " + str(e_info[0]) + ": " + str(e_info[1]) + "\n")
        sys.stderr.flush()
        return -1
    else:
        return 0
    finally:
        client.disconnect()


if __name__ == '__main__':
    main()
