log_dir = './data/'
log_file = 'fileData.txt'
found_max_depth = 2

import pandas as pd
import numpy as np

# Model building
from sklearn.model_selection import train_test_split
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score

# Model showing
import matplotlib.pyplot as plt
from sklearn import tree
from sklearn.tree import plot_tree

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
    n_pawns_kind = 3
    n_out_situations = 2
    model = RandomForestClassifier(max_depth=max_depth, n_estimators=n_pawns_kind, max_features=n_out_situations)
    np.random.seed(seed)

    model.fit(X, Y)
    return model

def show_accuracy(X, Y, model, set="default"):
    Y_model = model.predict(X)
    print(f"Accuracy score on {set} set is " + str(round(accuracy_score(Y, Y_model), 100)*100) + "%")
    return Y_model


def main():
    my_seed = 0
    my_random_state = 5

    dataset = to_dataframe(log_dir + log_file)

    X, Y = normalize_XY(*get_XY(dataset))
    Xtrain, Xtest, Ytrain, Ytest = train_test_split(X, Y, random_state=my_random_state)

    model = get_model(Xtrain, Ytrain, my_seed)

    Ytrain_model = show_accuracy(Xtrain, Ytrain, model, set="training")

    Ytest_model = show_accuracy(Xtest, Ytest, model, set="test")

    # plt.figure(figsize=(10, 10))
    # plot_tree(model, filled=True, feature_names=np.arange(243), class_names=['BlackWin', 'WhiteWin'], rounded=True, proportion = True)
    # plt.show()

    n_estimators = 3
    fig, axes = plt.subplots(nrows = 1,ncols = 3,figsize = (10,2), dpi=900)

    for index in range(0, n_estimators):
        tree.plot_tree(model.estimators_[index],
                    feature_names = np.arange(243), 
                    class_names=["blackWin", "whiteWin"],
                    filled = True,
                    ax = axes[index]);

        axes[index].set_title('Estimator: ' + str(index), fontsize = 11)
    fig.savefig('rf_5trees.png')

if __name__ == "__main__":
    main()