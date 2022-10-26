# TablutCompetition
Software for the Tablut Students Competition

## Installation on Ubuntu/Debian 

From console, run these commands to install JDK 8 e ANT:

```
sudo apt update
sudo apt install openjdk-8-jdk -y
sudo apt install ant -y
```

Now, clone the project repository:

```
git clone https://github.com/AGalassi/TablutCompetition.git
```

## Run the Server without Eclipse

The easiest way is to utilize the ANT configuration script from console.
Go into the project folder (the folder with the `build.xml` file):
```
cd TablutCompetition/Tablut
```

Compile the project:

```
ant clean
ant compile
```

The compiled project is in  the `build` folder.
Run the server with:

```
ant server
```

Check the behaviour using the random players in two different console windows:

```
ant randomwhite

ant randomblack
```

At this point, a window with the game state should appear.

To be able to run other classes, change the `build.xml` file and re-compile everything

To run RagnaBot use:

```
ant ragnabot -Dargs="WHITE {playerName} {timeout} {ipAddress}"

ant ragnabot -Dargs="BLACK  {playerName} {timeout} {ipAddress}"
```

In alternative, a `ragnabot.sh` file has been provided:

```
./Tablut/ragnabot.sh WHITE {playerName} {timeout} {ipAddress}

./Tablut/ragnabot.sh BLACK {playerName} {timeout} {ipAddress}
```


## Replay function

Replay a game using the logfile

Example:

```
java -jar .\server.jar -g -R .\logs\PLAYER1_vs_PLAYER2_1652711382324_gameLog.txt
```

