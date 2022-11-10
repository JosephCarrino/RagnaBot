import sys
from model_builder import predict_from_string


def read_from_pipe():
    return sys.stdin.readline().strip()


def write_to_pipe(msg):
    sys.stdout.write(msg + "\n")
    sys.stdout.flush()


if __name__ == "__main__":
    s = read_from_pipe()
    while s not in ['QUIT']:
        result = predict_from_string(s)
        #msg = str(result[0][0]) + "," + str(result[0][1])
        msg = f"{1 - result[0]},{result[0]}"
        write_to_pipe(msg)
        s = read_from_pipe()

    sys.exit(1)
