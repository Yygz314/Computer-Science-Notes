/*
 * @LastEditors  : Yygz314 2711859393@qq.com
 * @LastEditTime : 2026-03-15 19:41:35
 * @Author       : Yygz314
 * @Date         : 2026-03-15 19:40:25
 * @blog         : https://yygz314.github.io/
 */
#include<iostream>
#include<vector>
using namespace std;
class Solution {
public:
    int Find(vector<int>& parent,int index){
        if(parent[index]!=index){
            parent[index] = Find(parent,parent[index]);
        }
        return parent[index];
    }
    void Union(vector<int>& parent,int index1,int index2){
        parent[Find(parent,index1)] = Find(parent,index2);
    }
    int findCircleNum(vector<vector<int>>& isConnected) {
        int cities = isConnected.size();
        vector<int> parent(cities);
        for(int i = 0;i<cities;i++){
            parent[i] = i;
        }
        for(int i = 0;i<cities;i++){
            for(int j = i+1;j<cities;j++){
                if(isConnected[i][j] == 1){
                    Union(parent,i,j);
                }
            }
        }
        int provices = 0;
        for(int i = 0;i<cities;i++){
            if(parent[i] == i) provices++;
        }
        return provices;
    }
};
int main(){
    vector<vector<int>> isConnected = {{1,1,0},{1,1,0},{0,0,1}};
    Solution sol;
    cout<<sol.findCircleNum(isConnected);
    return 0;

}