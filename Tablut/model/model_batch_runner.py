import sys
from model_builder import predict_from_string


def run_prediction_on_file(file_name):
    with open(file_name, 'r') as f:
        data = f.readlines()

    values = []
    for d in data:
        values.append(predict_from_string(d.strip()))

    for value in values:
        print(str(value[0][0]) + "," + str(value[0][1]) + ";")


if __name__ == "__main__":
    run_prediction_on_file(str(sys.argv[1]))
