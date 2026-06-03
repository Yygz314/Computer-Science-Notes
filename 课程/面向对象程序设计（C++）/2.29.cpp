/*
 * @Author       : Yygz314
 * @Date         : 2025-02-28 13:28:05
 * @LastEditors  : Yygz314 2711859393@qq.com
 * @LastEditTime : 2025-02-28 13:32:36
 * @FilePath     : \VScode\homework_cpp\2.29.cpp
 */
#include <iostream>
using namespace std;
int main(){
    int count=0;
    for(int i=0;i<=100;i++){
        for(int j=0;j<=50;j++){
            for(int t=0;t<=20;t++){
                if(i+j*2+t*5==100){
                    cout<<"一元可以兑换成"<<i<<"个1分，"<<j<<"个2分，"<<t<<"个5分。"<<endl;
                    count++;
                }
            }
        }
    }
    cout<<"共有"<<count<<"种兑换方法。"<<endl;
    return 0;
}