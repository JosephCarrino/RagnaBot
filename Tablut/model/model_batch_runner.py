import sys
from model_builder import predict_from_string


def run_prediction_on_file(file_name):
    with open(file_name, 'r') as f:
        data = f.readlines()

    for d in data:
        predict_from_string(d.strip())


if __name__ == "__main__":
    run_prediction_on_file(str(sys.argv[1]))
