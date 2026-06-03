/*
 * @LastEditors  : Yygz314 2711859393@qq.com
 * @LastEditTime : 2026-04-15 11:13:09
 * @Author       : Yygz314
 * @Date         : 2026-04-15 11:12:38
 * @blog         : https://yygz314.github.io/
 */
#include<bits/stdc++.h>
using namespace std;
class Solution {
public:
    vector<int> temp;
    vector<vector<int>> ans;
    void dfs(int cur, int n, int k){
        if(temp.size() + (n-cur+ 1) < k ){
            return;
        }
        if(temp.size() == k){
            ans.push_back(temp);
            return;
        }
        temp.push_back(cur);
        dfs(cur + 1,n,k);
        temp.pop_back();
        dfs(cur+1,n,k);
    }
    vector<vector<int>> combine(int n, int k) {
        dfs(1,n,k);
        return ans;
    }
};

int main(){
    Solution s;
    vector<vector<int>> ans = s.combine(4,2);
    for(auto i: ans){
        for(auto j: i){
            cout<<j<<" ";
        }
        cout<<endl;
    }
    return 0;
}