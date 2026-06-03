/*
 * @Author       : Yygz314
 * @Date         : 2025-03-07 12:15:24
 * @LastEditors  : Yygz314 2711859393@qq.com
 * @blog         : https://www.cnblogs.com/Yygz314
 * @LastEditTime : 2025-03-07 12:22:01
 */
#include<iostream>
#include<cmath>
using namespace std;
double sroot(int x){
    return sqrt(x);
}
double sroot(long long x){
    return sqrt(x);
}
double sroot(double x){
    return sqrt(x); 
}
int main(){
    //вд15ЮЊР§
    cout<<sroot(15)<<endl;
    cout<<sroot(15ll)<<endl;
    cout<<sroot(15.0)<<endl;
    return 0;
}