import sys
from model_builder import predict_from_string, load_model, string_to_dataframe, string_to_input


def read_from_pipe():
    return sys.stdin.readline().strip()


def write_to_pipe(msg):
    sys.stdout.write(msg + "\n")
    sys.stdout.flush()


if __name__ == "__main__":

    if len(sys.argv) > 1:
        model_path = str(sys.argv[1])
    else:
        raise Exception("Not enough argument given")

    model = load_model(model_path)



    s = read_from_pipe()
    while s not in ['QUIT']:
        df = string_to_input(s)
        result = model.predict(df)

        write_to_pipe(str(result[0]))
        s = read_from_pipe()

    sys.exit(1)
