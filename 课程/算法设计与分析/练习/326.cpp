#include <iostream>
using namespace std;


class Solution {
public:
    bool isPowerOfThree(int n) {
        while (n && n % 3 == 0) {
            n /= 3;
        }
        return n == 1;
    }
};

int main() {
    Solution sol;
    int testCases[] = {1, 3, 9, 27, 0, -3, 2, 5, 1162261467};
    int caseNum = sizeof(testCases) / sizeof(testCases[0]);

    for (int i = 0; i < caseNum; ++i) {
        int num = testCases[i];
        bool res = sol.isPowerOfThree(num);
        cout <<res<< endl;
    }

    return 0;
}