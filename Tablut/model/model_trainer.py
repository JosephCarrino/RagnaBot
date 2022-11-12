import sys
from model_builder import train_and_save_model


if __name__ == "__main__":
    if len(sys.argv) > 2:
        dataset_path = str(sys.argv[1])
        model_path = str(sys.argv[2])
    else:
        raise Exception("Not enough argument given")

    train_and_save_model(dataset_path, model_path)
    print("Training finished")
