//============================================================================
// Name        : VectorSorting.cpp
// Author      : Daniel Little
// Version     : 2.0
// Copyright   : Copyright © 2017 SNHU COCE
// Description : Vector Sorting with Additional Functionality
// 
// Intent      : This application sorted using quicksort and selection sort
//               to sort through bids in a CSV file. The application can now
//               sort through added bids by date and bidID. The user also now
//               has the ability to add a bid by inputting the attributes
//               needed.
//             
//============================================================================

#include <algorithm>
#include <iostream>
#include <time.h>

#include "CSVparser.hpp"


using namespace std;

//============================================================================
// Global definitions visible to all methods and classes
//============================================================================

// forward declarations
double strToDouble(string str, char ch);

// define a structure to hold bid information
struct Bid {
    string bidId; // unique identifier
    string title;
    string closingDate;
    string fund;
    double amount;
    Bid() {
        amount = 0.0;
    }
};

// define a structure for date objects
struct Date {
    int month;
    int day;
    int year;
};

//============================================================================
// Static methods used for testing
//============================================================================

/**
 * Display the bid information to the console (std::out)
 *
 * @param bid struct containing the bid info
 */
void displayBid(Bid bid) {
    cout << bid.bidId << ": " << bid.title << " | " << bid.amount << " | " <<
       bid.closingDate << " | " << bid.fund << endl;
    return;
}

/**
 * Prompt user for bid information using console (std::in)
 *
 * @return Bid struct containing the bid info
 */
Bid getBid() {
    Bid bid;

    cout << "Enter Id: ";
    cin.ignore();
    getline(cin, bid.bidId);

    cout << "Enter title: ";
    getline(cin, bid.title);

    cout << "Enter closing date: ";
    getline(cin, bid.closingDate);

    cout << "Enter fund: ";
    cin >> bid.fund;

    cout << "Enter amount: ";
    cin.ignore();
    string strAmount;
    getline(cin, strAmount);
    bid.amount = strToDouble(strAmount, '$');

    return bid;
}

/**
 * Load a CSV file containing bids into a container
 *
 * @param csvPath the path to the CSV file to load
 * @return a container holding all the bids read
 */
vector<Bid> loadBids(string csvPath) {
    cout << "Loading CSV file " << csvPath << endl;

    // Define a vector data structure to hold a collection of bids.
    vector<Bid> bids;

    // initialize the CSV Parser using the given path
    csv::Parser file = csv::Parser(csvPath);

    try {
        // loop to read rows of a CSV file
        for (int i = 0; i < file.rowCount(); i++) {

            // Create a data structure and add to the collection of bids
            Bid bid;
            bid.bidId = file[i][1];
            bid.title = file[i][0];
            bid.closingDate = file[i][3];
            bid.fund = file[i][8];
            bid.amount = strToDouble(file[i][4], '$');

            //cout << "Item: " << bid.title << ", Fund: " << bid.fund << ", Amount: " << bid.amount << endl;

            // push this bid to the end
            bids.push_back(bid);
        }
    }
    catch (csv::Error& e) {
        std::cerr << e.what() << std::endl;
    }
    return bids;
}

/**
 * Partition the vector of bids into two parts, low and high
 *
 * @param bids Address of the vector<Bid> instance to be partitioned
 * @param begin Beginning index to partition
 * @param end Ending index to partition
 */
int partition(vector<Bid>& bids, int begin, int end) {
    int low = begin;
    int high = end;

    int pivot = begin + (end - begin) / 2;

    bool done = false;
    while (!done) {
        //while it remains smaller than the pivot, it still keeps increasing the low
        while (bids.at(low).title.compare(bids.at(pivot).title) < 0) {
            ++low;
        }

        //while bids at high is smaller than bids at pivot, it subtracts from its
        while (bids.at(pivot).title.compare(bids.at(high).title) < 0) {
            --high;
        }

        if (low >= high) {
            done = true;
        }
        else {
            //swaps the low and high bids
            swap(bids.at(low), bids.at(high));

            //this moves the points closer to eachother
            ++low;
            --high;
        }

    }
    return high;
}

/**
 * Perform a quick sort on bid title
 * Average performance: O(n log(n))
 * Worst case performance O(n^2))
 *
 * @param bids address of the vector<Bid> instance to be sorted
 * @param begin the beginning index to sort on
 * @param end the ending index to sort on
 */
void quickSort(vector<Bid>& bids, int begin, int end) {
    int mid = 0;
    //finish if if there are no bids.
    if (begin >= end) {
        return;
    }

    //this partitions the bids into the low and high sections.
    mid = partition(bids, begin, end);

    //recursive call using midpoint
    quickSort(bids, begin, mid);

    //recursive call using midpoint + 1
    quickSort(bids, mid + 1, end);
}

/**
 * Perform a selection sort on bid title
 * Average performance: O(n^2))
 * Worst case performance O(n^2))
 *
 * @param bid address of the vector<Bid>
 *            instance to be sorted
 */
void selectionSort(vector<Bid>& bids) {
    int min; //for the minimum bid

    //position sorted and unsorted bids
    for (unsigned pos = 0; pos < bids.size(); ++pos) {
        min = pos;

        for (unsigned j = pos + 1; j < bids.size(); ++j) {
            if (bids.at(j).title.compare(bids.at(min).title) < 0) {
                min = j;
            }
        }
        if (min != pos) {
            swap(bids.at(pos), bids.at(min));
        }
    }
}

/**
 * Simple C function to convert a string to a double
 * after stripping out unwanted char
 *
 * credit: http://stackoverflow.com/a/24875936
 *
 * @param ch The character to strip out
 */
double strToDouble(string str, char ch) {
    str.erase(remove(str.begin(), str.end(), ch), str.end());
    return atof(str.c_str());
}

/*
 Function to compare two dates
 returns true if date1 is older than date2
*/

bool compareDates(string date1, string date2) {
    Date firstDate;
    Date secondDate;
    char discard; // To discard '/'
    istringstream iss(date1);
    iss >> firstDate.month >> discard >> firstDate.day >> discard >> firstDate.year;
    istringstream iss2(date2);
    iss2 >> secondDate.month >> discard >> secondDate.day >> discard >> secondDate.year;
    if (firstDate.year != secondDate.year) {
        return firstDate.year < secondDate.year;
    }
    if (firstDate.month != secondDate.month) {
        return firstDate.month < secondDate.month;
    }
    return firstDate.day < secondDate.day;
}

/* Helper function to compare dates for bids */

bool compareBidDates(Bid bid1, Bid bid2) {
    return compareDates(bid1.closingDate, bid2.closingDate);
}

/**
 * The one and only main() method
 */
int main(int argc, char* argv[]) {

    // process command line arguments
    string csvPath;
    switch (argc) {
    case 2:
        csvPath = argv[1];
        break;
    default:
        csvPath = "eBid_Monthly_Sales_Dec_2016.csv";
    }

    // Define a vector to hold all the bids
    vector<Bid> bids;

    // Define a timer variable
    clock_t ticks;

    int choice = 0;
    while (choice != 9) {
        cout << "Menu:" << endl;
        cout << "  1. Load Bids" << endl;
        cout << "  2. Display All Bids" << endl;
        cout << "  3. Selection Sort All Bids" << endl;
        cout << "  4. Quick Sort All Bids" << endl;
        cout << "  5. Insert a New Bid" << endl;
        cout << "  6. Sort by Closing Dates" << endl; 
        cout << "  9. Exit" << endl;
        cout << "Enter choice: ";
        cin >> choice;

        switch (choice) {

        case 1:
            // Initialize a timer variable before loading bids
            ticks = clock();

            // Complete the method call to load the bids
            bids = loadBids(csvPath);

            cout << bids.size() << " bids read" << endl;

            // Calculate elapsed time and display result
            ticks = clock() - ticks; // current clock ticks minus starting clock ticks
            cout << "time: " << ticks << " clock ticks" << endl;
            cout << "time: " << ticks * 1.0 / CLOCKS_PER_SEC << " seconds" << endl;

            break;

        case 2:
            // Loop and display the bids read
            for (int i = 0; i < bids.size(); ++i) {
                displayBid(bids[i]);
            }
            cout << endl;

            break;

        case 3:
            // Initialize a timer variable before loading the bids
            ticks = clock();

            // Complete the method call to load the bids
            selectionSort(bids);

            cout << bids.size() << " bids read" << endl;

            // Calculate elapsed time and display result
            ticks = clock() - ticks; // current clock ticks minus starting clock ticks
            cout << "time: " << ticks << " clock ticks" << endl;
            cout << "time: " << ticks * 1.0 / CLOCKS_PER_SEC << " seconds" << endl;

            break;

        case 4:
            // Initialize a timer variable before loading bids
            ticks = clock();

            // Complete the method call to load the bids
            quickSort(bids, 0, bids.size() - 1);

            cout << bids.size() << " bids read" << endl;

            // Calculate elapsed time and display result
            ticks = clock() - ticks; // current clock ticks minus starting clock ticks
            cout << "time: " << ticks << " clock ticks" << endl;
            cout << "time: " << ticks * 1.0 / CLOCKS_PER_SEC << " seconds" << endl;

            break;

        case 5: 
            bids.push_back(getBid());
            break;

        case 6:
            sort(bids.begin(), bids.end(), compareBidDates);
            break;
        }
    }

    cout << "Good bye." << endl;

    return 0;
}
