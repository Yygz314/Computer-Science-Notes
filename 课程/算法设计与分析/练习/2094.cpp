/*
 * @LastEditors  : Yygz314 2711859393@qq.com
 * @LastEditTime : 2026-04-08 10:43:50
 * @Author       : Yygz314
 * @Date         : 2026-04-08 10:43:12
 * @blog         : https://yygz314.github.io/
 */
#include<bits/stdc++.h>
using namespace std;
class Solution {
public:
    bool isanser(int n,int num[]){
        int temp[10];
        for(int i = 0;i<10;i++) temp[i]=num[i];
        while(n>0){
            if(temp[n%10]==0) return false;
            temp[n%10]--;
            n = n/10;
        }
        return true;
    }
    vector<int> findEvenNumbers(vector<int>& digits) {
        vector<int> ans;
        int num[10]={0};
        for(int i = 0;i<digits.size();i++){
            num[digits[i]]++;
        }
        for(int i = 100;i<999;i+=2){
            if(isanser(i,num)) ans.push_back(i);
        }
        return ans;
    }
};
int main(){
    vector<int> digits = {2,1,3,0};
    Solution sol;
    vector<int> ans = sol.findEvenNumbers(digits);
    for(int i = 0;i<ans.size();i++) cout<<ans[i]<<" ";
    return 0;
}