/* Copyright Bryan Hayes 2019
 * 
 * File:   hayesbm3_hw4.cpp
 * Author: Bryan Hayes 
 *
 * Created on September 22, 2019, 7:32 PM
 * 
 * Description: This program is intended to build a basic command shell. 
 */

#include <sys/wait.h>
#include <boost/algorithm/string.hpp>
#include <cstdlib>
#include <string>
#include <iostream>
#include <fstream>
#include <iomanip>
#include <sstream>
#include <vector>


using namespace std;

/*
 * 
 */

// A method for executing linux commands and returning the exit code.
void myExec(vector<string> argList) {
    std::vector<char*> args;
    for (size_t i = 0; (i < argList.size()); i++) {
        args.push_back(&argList[i][0]);
    }  
    string commands = "";
    for (size_t i = 1; i < argList.size(); i++) {
        commands += argList[i] + " ";
    }
    args.push_back(nullptr);
    cout << "Running: " << argList[0] << " " << commands << endl;
    const int pid = 0;
    int exitCode = 0;
    execvp(args[0], &args[0]);
    waitpid(pid, &exitCode, 0);
    cout << "Exit code: " << exitCode << endl;
}

// Both executes a given command and forks to a child process. 
int forkNexec(vector<string> argList) {
    std::vector<char*> args;
    stringstream stream;
    
    for (size_t i = 0; (i < argList.size()); i++) {
        args.push_back(&argList[i][0]);
    }  
    string commands = "";
    for (size_t i = 1; i < argList.size(); i++) {
        commands += argList[i] + " ";
    }
    args.push_back(nullptr);
    cout << "Running: " << argList[0] << " " << commands << endl;
    const int pid = fork();
    if (pid == 0) {
        execvp(args[0], &args[0]);
    } else {
        return pid;
    }
    return 0;
}
// Gets commands from a file and sends them to the forkNexec method to be 
// executed, one command after another.
void serialExec(ifstream& fileStream, string fileName) {
    fileStream.open(fileName);
    string line;
    vector<string> argList;
    while (getline(fileStream, line)) {
        if (line.substr(0, 1) == "#") {
        } else if (line == "") {
        } else {
            boost::algorithm::trim(line);
            boost::split(argList, line, boost::is_any_of("\t "),
                    boost::token_compress_on);
            int pid = forkNexec(argList);
            int exitCode = 0;
            waitpid(pid, &exitCode, 0);
            cout << "Exit code: " << exitCode << endl;
        }
    }
}

// Gets commands from a file and sends them to the myExec method to be executed,
// waiting for each command to be run in order before printing exit codes. 
void parallelExec(ifstream& fileStream, string fileName) {
    fileStream.open(fileName);
    string line;
    vector<string> argList;
    vector<int> pids;
    while (getline(fileStream, line)) {
        if (line.substr(0, 1) == "#") {
        } else if (line == "") {
        } else {
            boost::algorithm::trim(line);
            boost::split(argList, line, boost::is_any_of("\t "),
                    boost::token_compress_on);
            int pid = forkNexec(argList);
            pids.push_back(pid);
        }
    }
    for (int pid : pids) {
        int exitCode = 0;
        waitpid(pid, &exitCode, 0);
        cout << "Exit code: " << exitCode << endl;
    }
}

// Cleans the argument string so that all quotes are removed. 
string removeQuotes(string args) {
    for (size_t i = 0; i < args.size(); i++) {
        if (args.at(i) == '"') {
            args.erase(i, 1);
        }
    }
    return args;
}

int main(int argc, char** argv) {
    std::string line;
    ifstream fileStream;
    while (getline(cin, line)) {
        line = removeQuotes(line);
        vector<string> argList;
        boost::algorithm::trim(line);
        boost::split(argList, line, boost::is_any_of("\t "), 
                boost::token_compress_on);
         if (argList[0] == "exit") {
            return 0;
        } else if (line == "") {
        } else if (line.substr(0, 1) == "#") {
        } else if (argList[0] == "SERIAL") {
            serialExec(fileStream, argList[1]);
        } else if (argList[0] == "PARALLEL") {
            parallelExec(fileStream, argList[1]);
        } else {
            int pid = forkNexec(argList), exitCode = 0;
            waitpid(pid, &exitCode, 0);
            cout << "Exit code: " << exitCode << endl;
        }
    }
    return 0;
}

