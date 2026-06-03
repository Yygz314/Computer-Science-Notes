/*
 * @Author       : Yygz314
 * @Date         : 2025-02-28 13:22:36
 * @LastEditors  : Yygz314 2711859393@qq.com
 * @LastEditTime : 2025-02-28 13:27:21
 * @FilePath     : \VScode\homework_cpp\2.27.cpp
 */
#include<iostream>
using namespace std;
int main(){
    int *num;
    num=new int[25];
    num[0]=1,num[1]=1;
    for(int i=2;i<20;i++){
        num[i]=num[i-1]+num[i-2];
    }
    cout<<"The first 20 fibonacci number are: ";
    for(int i=0;i<20;i++){
        cout<<num[i]<<" "; 
    }
    cout<<endl;
    delete []num;
    return 0;
}