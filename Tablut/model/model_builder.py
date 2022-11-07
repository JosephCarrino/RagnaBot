log_dir = './data/'
log_file = 'piedino-vs-becchi1.txt'
found_max_depth = 2

import pandas as pd
import numpy as np
import sys
import os

# Model building
from sklearn.model_selection import train_test_split
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score

# Model showing
import matplotlib.pyplot as plt
from sklearn import tree
from sklearn.tree import plot_tree
from sklearn.utils import Bunch

#Model exporting
from sklearn2pmml import sklearn2pmml
from sklearn2pmml.pipeline import PMMLPipeline
import pickle

curr_dir = os.path.dirname(os.path.abspath(__file__))
filename = f"{curr_dir}/my_model.sav"

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
    dataset = {'black' : [], 'white' : [], 'king' : [], 'result' : []}
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
    true_X = np.zeros((X.shape[0], 3*9*9), dtype=np.float32)

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

def get_model(X, Y, seed, max_depth = found_max_depth):
    n_pawns_kind = 9
    n_out_situations = 3
    model = RandomForestClassifier(max_depth=max_depth, n_estimators=n_pawns_kind, max_features=n_out_situations)
    np.random.seed(seed)

    clf = PMMLPipeline(
        [
            (
                "classifier",
                model,
            )
        ]
    )
    # print(X.shape, Y.shape)
    # X_names = np.arange(X.shape[1])
    # for i in range(X_names.shape[0]):
    #     X_names[i] = str(X_names[i])
    # Y_names = np.array(["outcome"])
    # X_names = np.reshape(X_names, (X_names.shape[0], 1))
    # Y_names = np.reshape(Y_names, (Y_names.shape[0], 1))
    # Y = np.reshape(Y, (Y.shape[0], 1))
    # print(X_names.shape, Y_names.shape)
    # X_tot = np.concatenate((X, X_names.T), axis=0)
    # Y_tot = np.concatenate((Y, Y_names.T), axis=0)
    # print(X_tot.shape, Y_tot.shape)
    # to_use = Bunch(data=X, target=Y, feature_names = ["X" + str(i) for i in range(X.shape[0])], target_names = ["Y"])
    # print(to_use)
    df = {}
    for i in range(X.shape[1]):
        df["X"+str(i)] = X[:,i]
    df["Y"] = Y
    my_df = pd.DataFrame(df)
    real_X = my_df.drop('Y', axis=1)
    real_Y = my_df["Y"]
    
    clf.fit(real_X, real_Y)
    return clf, model

def show_accuracy(X, Y, model, set="default"):
    Y_model = model.predict(X)
    print(f"Accuracy score on {set} set is " + str(round(accuracy_score(Y, Y_model), 100)*100) + "%")
    return Y_model

def load_model(filename):
    return pickle.load(open(filename, "rb"))

def predict_from_string(X):
    X = list(X)
    df = {}
    for i in range(len(X)):
        df["X" + str(i)] = int(X[i])
    my_df = pd.DataFrame(df, index=[0])
    result = predict(my_df)
    print(str(result[0][0]) + "," + str(result[0][1]) + ";")
    return result

def predict(X):
    model = load_model(filename)
    Y = model.predict_proba(X)
    return Y

def main():
    # my_seed = 0
    # my_random_state = 5

    # dataset = to_dataframe(log_dir + log_file)

    # X, Y = normalize_XY(*get_XY(dataset))
    # Xtrain, Xtest, Ytrain, Ytest = train_test_split(X, Y, random_state=my_random_state)

    # model, model_to_plot = get_model(Xtrain, Ytrain, my_seed)

    # Ytrain_model = show_accuracy(Xtrain, Ytrain, model, set="training")

    # Ytest_model = show_accuracy(Xtest, Ytest, model, set="test")

    # # plt.figure(figsize=(10, 10))
    # # plot_tree(model, filled=True, feature_names=np.arange(243), class_names=['BlackWin', 'WhiteWin'], rounded=True, proportion = True)
    # # plt.show()

    # n_estimators = 3
    # fig, axes = plt.subplots(nrows = 1,ncols = 3,figsize = (10,2), dpi=900)

    # for index in range(0, n_estimators):
    #     tree.plot_tree(model_to_plot.estimators_[index],
    #                 feature_names = np.arange(243), 
    #                 class_names=["blackWin", "whiteWin"],
    #                 filled = True,
    #                 ax = axes[index]);

    #     axes[index].set_title('Estimator: ' + str(index), fontsize = 11)
    # fig.savefig('rf_5trees.png')

    # sklearn2pmml(model, "boosting_model.pmml", with_repr=True)
    # my_x = np.reshape(Xtest[12, :], (243, -1))
    # print(my_x.shape)
    # predict(my_x.T)
    if (len(sys.argv) == 1):
        print("No serialized state given")
        predict_from_string("000000011111001000000010000000000000000000100000010000000000010000001000000000000000000000000000000000000000010000000000000000010000000000000000110000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000")
    else:
        predict_from_string(sys.argv[1])
    # pickle.dump(model, open(filename, 'wb'))

if __name__ == "__main__":
    main()