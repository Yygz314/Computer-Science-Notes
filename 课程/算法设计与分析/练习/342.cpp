#include <iostream>
using namespace std;

class Solution {
public:
    bool isPowerOfFour(int n) {
        return n > 0 && (n & (n - 1)) == 0 && n % 3 == 1;
    }
};

int main() {
    Solution sol;
    int testNums[] = {1, 4, 16, 64, 0, -4, 2, 8, 32, 5};
    int count = sizeof(testNums) / sizeof(testNums[0]);
    for (int i = 0; i < count; i++) {
        int num = testNums[i];
        bool res = sol.isPowerOfFour(num);
        cout << res << endl;
    }
    return 0;
}