#include <iostream>
#include <time.h>
#include <conio.h>
#include <map>
#include <vector>
#include <string>
#include <algorithm>
using namespace std;

#define sz(a) int((a).size())
#define ll long long
#define pb push_back
#define endl "\n"

/*
#define input(a, n)                   \
    for (long long i = 0; i < n; i++) \
    cin >> a[i]

#define output(a, n)                  \
    for (long long i = 0; i < n; i++) \
    cout << a[i] << " "

#define she_taught_me_to_code         \
    cin.tie(0);                       \
    cout.tie(0);                      \
    ios_base::sync_with_stdio(false); \
    cin.tie(NULL);                    \
    clock_t startTime = clock();
#define time cout << (double(clock() - startTime) / (double)CLOCKS_PER_SEC) * 1000 << " ms" << endl;
//#define debug(k) cout << "\t-> " << #k << " = " << k << endl;
*/

ll min(ll a, ll b)
{
    return a < b ? a : b;
}

ll max(ll a, ll b)
{
    return a > b ? a : b;
}

struct board
{
    char arr[3][3];
    int score;
    int index;
    board()
    {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                arr[i][j] = '-';
    }
};

vector<int> game[1000000];
vector<board> store;
map<int, int> mapping; // from index to store index

char check(board b) // terminal state check
{
    for (int i = 0; i < 3; i++)
    {
        int count = 0;
        for (ll j = 0; j < 3; j++)
            if (b.arr[i][j] == 'X')
                count++;

        if (count == 3)
            return 'X';

        count = 0;

        for (ll j = 0; j < 3; j++)
            if (b.arr[i][j] == 'O')
                count++;

        if (count == 3)
            return 'O';

        count = 0;

        for (ll j = 0; j < 3; j++)
            if (b.arr[j][i] == 'O')
                count++;

        if (count == 3)
            return 'O';

        count = 0;
        for (ll j = 0; j < 3; j++)
            if (b.arr[j][i] == 'X')
                count++;

        if (count == 3)
            return 'X';
    }

    if (b.arr[0][0] == 'X' && b.arr[1][1] == 'X' && b.arr[2][2] == 'X')
        return 'X';

    if (b.arr[0][0] == 'O' && b.arr[1][1] == 'O' && b.arr[2][2] == 'O')
        return 'O';

    if (b.arr[0][2] == 'X' && b.arr[1][1] == 'X' && b.arr[2][0] == 'X')
        return 'X';

    if (b.arr[0][2] == 'O' && b.arr[1][1] == 'O' && b.arr[2][0] == 'O')
        return 'O';

    int count = 0;
    for (int i = 0; i < 3; i++)
        for (int j = 0; j < 3; j++)
            if (b.arr[i][j] == '-')
                count++;

    if (count == 0)
        return 'D';
    else
        return '-';
}

int len = 0, win = 0, lose = 0, draw = 0;

void build(board b, char turn, int index, int depth) // build game tree
{
    if (check(b) == 'X')
    {
        store[mapping[index]].score = 10;
        win++;
        return;
    }

    if (check(b) == 'O')
    {
        store[mapping[index]].score = -10;
        lose++;
        return;
    }
    if (check(b) == 'D')
    {
        store[mapping[index]].score = 0;
        draw++;
        return;
    }

    int ma = -10000, mi = 10000;

    if (turn == 'C')
    {
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                if (b.arr[i][j] == '-')
                {
                    board next = b;
                    next.arr[i][j] = 'X';
                    next.index = len;
                    store.pb(next);
                    mapping[len] = store.size() - 1;
                    len++;
                    game[index].pb(len - 1);
                    int x = len - 1;
                    build(next, 'P', len - 1, depth + 1);
                    ma = max(ma, store[mapping[x]].score);
                }
            }
        }
        store[mapping[index]].score = ma - depth;
    }
    if (turn == 'P')
    {
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                if (b.arr[i][j] == '-')
                {
                    board next = b;
                    next.arr[i][j] = 'O';
                    next.index = len;
                    store.pb(next);
                    mapping[len] = store.size() - 1;
                    len++;
                    game[index].pb(len - 1);
                    int x = len - 1;
                    build(next, 'C', len - 1, depth + 1);
                    mi = min(mi, store[mapping[x]].score);
                }
            }
        }
        store[mapping[index]].score = mi + depth;
    }
}

void print(board b)
{
    for (int i = 0; i < 3; i++)
    {
        for (int j = 0; j < 3; j++)
            cout << b.arr[i][j] << " ";
        cout << endl;
    }
    cout << endl;
}

bool compare(board b1, board b2)
{
    int count = 0;
    for (int i = 0; i < 3; i++)
    {
        for (int j = 0; j < 3; j++)
            if (b1.arr[i][j] == b2.arr[i][j])
                count++;
    }
    return count == 9;
}

bool over(board b) // checking exit condition
{
    if (check(b) == 'X')
    {
        print(b);
        cout << "You Lost\n";
        // getch();
        return 1;
    }

    if (check(b) == 'O') // this will never happen, just written for the sake of completeness.
    {
        print(b);
        cout << "You Win\n";
        // getch();
        return 1;
    }

    if (check(b) == 'D')
    {
        print(b);
        cout << "Tie\n";
        // getch();
        return 1;
    }
    return 0;
}

int main()
{
    board b;
    b.index = 0;
    store.pb(b);
    mapping[0] = 0;
    store.pb(b);
    len++;
    build(b, 'P', 0, 0);
    map<int, pair<int, int>> m;
    m[1] = {0, 0}; //    1 2 3
    m[2] = {0, 1}; //    4 5 6
    m[3] = {0, 2}; //    7 8 9
    m[4] = {1, 0};
    m[5] = {1, 1};
    m[6] = {1, 2};
    m[7] = {2, 0};
    m[8] = {2, 1};
    m[9] = {2, 2};
    cout << "\t\t\t\t\tYou can't beat me, but you can try!!\n";
    while (1)
    {
        if (over(b))
            return 0;

        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
                cout << b.arr[i][j] << " ";
            cout << endl;
        }
        int choose;
        cin >> choose;

        while (choose < 1 || choose > 9)
        {
            cout << "Invalid Move, Enter again\n";
            cin >> choose;
        }
        int i = m[choose].first;
        int j = m[choose].second;

        while (b.arr[i][j] != '-')
        {
            cout << "Invalid Move, Enter again\n";
            cin >> choose;
            i = m[choose].first;
            j = m[choose].second;
        }

        b.arr[i][j] = 'O';
        if (over(b))
            return 0;

        for (int i = 0; i < game[b.index].size(); i++)
        {
            if (compare(b, store[mapping[game[b.index][i]]]))
            {
                b = store[mapping[game[b.index][i]]];
                int ma = -1;
                board temp;

                for (int j = 0; j < game[b.index].size(); j++)
                {
                    if (store[mapping[game[b.index][j]]].score > ma)
                    {
                        ma = store[mapping[game[b.index][j]]].score;
                        temp = store[mapping[game[b.index][j]]];
                    }
                }
                b = temp;
                break;
            }
        }
    }
}
