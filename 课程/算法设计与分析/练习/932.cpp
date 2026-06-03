/*
 * @LastEditors  : Yygz314 2711859393@qq.com
 * @LastEditTime : 2026-04-01 11:18:58
 * @Author       : Yygz314
 * @Date         : 2026-04-01 11:13:46
 * @blog         : https://yygz314.github.io/
 */
#include <bits/stdc++.h>
using namespace std;
class Solution {
public:
    unordered_map<int,vector<int>>mp;
    vector<int> beautifulArray(int N) {
        vector<int> ans(N,0);
        int t = 0;
        if(mp.find(N)!=mp.end()){
            return mp[N];
        }
        if(N!=1){
            for(auto x:beautifulArray((N+1)/2)){
                ans[t++] = 2*x -1;
            }
            for(auto x:beautifulArray(N/2)){
                ans[t++] = 2*x;
            }
        }else{
            ans[0] = 1;
        }
        mp[N] = ans;
        return ans;
    }
};

int main(){
    int n;
    cin>>n;
    Solution sol;
    vector<int> ans = sol.beautifulArray(n);
    for(auto x:ans){
        cout<<x<<" ";
    }
    cout<<endl;
}