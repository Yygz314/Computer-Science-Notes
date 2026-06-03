/*
 * @LastEditors  : Yygz314 2711859393@qq.com
 * @LastEditTime : 2026-03-04 11:11:04
 * @Author       : Yygz314
 * @Date         : 2026-03-04 11:07:47
 * @blog         : https://yygz314.github.io/
 */
#include<bits/stdc++.h>
using namespace std;

int solve(string s,vector<string>& dic){
    int len = s.size();
    vector<int> dp(len+1,INT_MAX); 
    unordered_map<string,int> mp;
    for(auto w:dic){
        mp[w]++;
    } 
    dp[0] = 0;   
    for(int i = 1;i<=len;i++){
        dp[i] = dp[i-1]+1;
        for(int j = i-1;j>=0;j--){
            if(mp.count(s.substr(j,i-j))){
                dp[i] = min(dp[i],dp[j]);
            }
        }
    }
    return dp[len];
}


int main(){
    //数据输入
    vector<string> dic ;
    string s;   //字符串
    int n;  //字典数量
    cin>>s;
    cin>>n;
    for(int i = 0;i<n;i++){
        string temp;
        cin>>temp;
        dic.push_back(temp);
    }
    int ans = solve(s,dic);
    cout<<ans;
    return 0;
}