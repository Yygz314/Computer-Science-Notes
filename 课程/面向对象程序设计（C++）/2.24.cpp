/*
 * @Author       : Yygz314
 * @Date         : 2025-03-07 12:10:05
 * @LastEditors  : Yygz314 2711859393@qq.com
 * @blog         : https://www.cnblogs.com/Yygz314
 * @LastEditTime : 2025-03-07 12:15:13
 * @FilePath     : \VScode\homework_cpp\3-2.24.cpp
 */
#include<iostream>
using namespace std;
int i=15;
int main(){
    int i;
    i=100;
    ::i=i+1;
    cout<<::i<<endl;
    return 0;
}

//Êä³ö½á¹û£º101