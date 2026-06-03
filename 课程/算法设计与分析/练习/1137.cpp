/*
 * @LastEditors  : Yygz314 2711859393@qq.com
 * @LastEditTime : 2026-04-22 10:44:07
 * @Author       : Yygz314
 * @Date         : 2026-04-22 10:43:23
 * @blog         : https://yygz314.github.io/
 */
#include <bits/stdc++.h>
using namespace std;
class Solution {
public:
    int tribonacci(int n) {
        int a = 0,b = 1,c = 1;
        if(n==0) return 0;
        if(n==1||n==2) return 1;
        else{
            for(int i = 3;i<=n;i++){
                int temp = a+b+c;
                a = b;
                b = c;
                c = temp;
            }
            return c;
        }
    }
};
int main(){
    Solution s;
    cout<<s.tribonacci(4)<<endl;
    cout<<s.tribonacci(25)<<endl;
    return 0;
}