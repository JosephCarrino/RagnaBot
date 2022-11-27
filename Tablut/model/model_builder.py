log_dir = './data/'
log_file = 'piedino-vs-becchi.txt'
found_max_depth = 2

import pandas as pd
import numpy as np
import sys
import os

# Model building
from sklearn.model_selection import train_test_split
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier
from sklearn.neural_network import MLPRegressor
from sklearn.metrics import accuracy_score

# Model showing
import matplotlib.pyplot as plt
from sklearn import tree
from sklearn.tree import plot_tree
from sklearn.utils import Bunch

# Model exporting
from sklearn2pmml import sklearn2pmml
from sklearn2pmml.pipeline import PMMLPipeline
import pickle

curr_dir = os.path.dirname(os.path.abspath(__file__))
filename = f"{curr_dir}/regressor.sav"


# Utils that given a log file, returns turns and endings from that turn
def data_from_log(dir):
    games = []
    with open(dir) as f:
        games = [line.replace('\n', '') for line in f.readlines()]

    games_turns = []
    games_endings = []
    for game in games:
        states = game.split(';')
        games_turns.append(states[:-1])
        games_endings.append(states[-1])
    found_max_depth = len(games)
    return games_turns, games_endings


# Utils that given turns and their ending, returns single black, white and king matrices and their endings
def get_dataset_row(turns, ending):
    black_states = []
    white_states = []
    king_states = []
    for turn in turns:
        states = turn.split(',')
        black_states.append(states[0])
        white_states.append(states[1])
        king_states.append(states[2])
    return black_states, white_states, king_states, ending


## Given a .txt file log formatted with comma-separated matrices and semicolon-separated states, output is a pandas dataframe
def to_dataframe(logs_source):
    dataset = {'black': [], 'white': [], 'king': [], 'result': []}
    games_turns, games_endings = data_from_log(logs_source)
    for infos in zip(games_turns, games_endings):
        black_states, white_states, king_states, ending = get_dataset_row(infos[0], infos[1])
        for states in zip(black_states, white_states, king_states):
            dataset["black"].append(states[0])
            dataset["white"].append(states[1])
            dataset["king"].append(states[2])
            dataset["result"].append(int(ending))

    return pd.DataFrame(dataset)


def get_XY(dataset):
    return dataset.drop("result", axis=1), dataset["result"]


def normalize_XY(X, Y):
    X = np.array(X)
    Y = np.array(Y)
    lines = []
    for i in range(X.shape[0]):
        lines.append(str(X[i, 0]) + str(X[i, 1]) + str(X[i, 2]))
    true_X = np.zeros((X.shape[0], 3 * 9 * 9), dtype=np.float32)

    new_lines = []
    for elem in lines:
        elems = list(elem)
        single_elem = []
        for i in elems:
            single_elem.append(int(i))
        new_lines.append(single_elem)

    for i in range(len(new_lines)):
        for j in range(len(new_lines[i])):
            true_X[i, j] = int(new_lines[i][j])

    return true_X, Y


def get_model(X, Y, seed, max_depth=found_max_depth):
    n_pawns_kind = 9
    n_out_situations = 3
    # model = RandomForestClassifier(max_depth=max_depth, n_estimators=n_pawns_kind, max_features=n_out_situations)
    model = MLPRegressor(hidden_layer_sizes=(150, 20), learning_rate_init=0.0001, solver="adam", max_iter=400)

    #np.random.seed(seed)

    """
    clf = PMMLPipeline(
        [
            (
                "classifier",
                model,
            )
        ]
    )
    df = {}
    """

    #for i in range(X.shape[1]):
    #    df["X" + str(i)] = X[:, i]
    #df["Y"] = Y
    #my_df = pd.DataFrame(df)
    #real_X = my_df.drop('Y', axis=1)
    #real_Y = my_df["Y"]

    model = model.fit(X, Y)
    return model, model


def show_accuracy(X, Y, model, set="default"):
    Y_model = model.predict(X)
    print(f"Accuracy score on {set} set is " + str(round(accuracy_score(Y, Y_model), 100) * 100) + "%")
    return Y_model


def load_model(filename):
    return pickle.load(open(filename, "rb"))


def string_to_dataframe(s):
    X = list(s)
    df = {}
    for i in range(len(X)):
        df["X" + str(i)] = int(X[i])
    return pd.DataFrame(df, index=[0])


def string_to_input(s):
    return np.array([[int(c) for c in s]])


def predict_from_string(X):
    return predict(string_to_input(X))


def predict(X):
    model = load_model(filename)
    # Y = model.predict_proba(X)
    Y = model.predict(X)
    return Y


def train_and_save_model(dataset_path, model_file_path):
    my_seed = 0
    my_random_state = 5

    dataset = to_dataframe(dataset_path)

    X, Y = normalize_XY(*get_XY(dataset))

    Xtrain, Xtest, Ytrain, Ytest = train_test_split(X, Y, random_state=my_random_state)

    model, model_to_plot = get_model(Xtrain, Ytrain, my_seed)

    from sklearn.metrics import mean_squared_error

    # Ytrain_model = show_accuracy(Xtrain, Ytrain, model, set="training")

    # Ytest_model = show_accuracy(Xtest, Ytest, model, set="test")

    y_true = model.predict(Xtrain)
    print(f"Train: {mean_squared_error(Ytrain, y_true, squared=False)}")

    y_true = model.predict(Xtest)
    print(f"Test: {mean_squared_error(Ytest, y_true, squared=False)}")

    with open(model_file_path, "wb") as f:
        pickle.dump(model, f)


def main():
    if (len(sys.argv) == 1):
        print("No serialized state given")
        predict_from_string(
            "000000011111001000000010000000000000000000100000010000000000010000001000000000000000000000000000000000000000010000000000000000010000000000000000110000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000")
    else:
        predict_from_string(sys.argv[1])


if __name__ == "__main__":
    main()
