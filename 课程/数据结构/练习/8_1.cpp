/*
 * @LastEditors  : Yygz314 2711859393@qq.com
 * @LastEditTime : 2025-11-03 13:51:05
 * @Author       : Yygz314
 * @Date         : 2025-11-03 13:30:09
 * @blog         : https://www.cnblogs.com/Yygz314
 */
#include<iostream>
using namespace std;

int main(){
    int n;
    cin>>n;
    int num[n];
    for(int i=0;i<n;i++){
        cin>>num[i];
    } 
    int i = 0,j = n-1;
    while(i<j){
        while(i<j && num[i]>0) i++;
        while(i<j && num[j]<0) j--;
        if(i<j){
            int temp = num[i];
            num[i] = num[j];
            num[j] = temp;
        }
    }
    for(int i=0;i<n;i++){
        cout<<num[i]<<" ";
    }
    cout<<endl;
    return 0;
}