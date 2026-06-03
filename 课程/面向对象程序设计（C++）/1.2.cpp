/*
 * @Author: Yygz314 2711859393@qq.com
 * @Date: 2025-02-21 13:22:47
 * @LastEditors: Yygz314 2711859393@qq.com
 * @LastEditTime: 2025-02-21 13:59:41
 * @FilePath: \VScode\homework_cpp\1.2.cpp
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
#include<bits/stdc++.h>
using namespace std;
const int len = 100;
const int maxn = len+1;
int prime[maxn],count=0;  //数组存放所有素数，count统计素数个数
bool p[len+1]={false};  //标记i是否为素数（是否被筛去）
void findprime(){
    int count=0,ans=0;
    for(int i=2;i<=len;i++){
        if(p[i]==false){
            prime[count++]=i;
            for(int j=i+i;j<=len;j+=i){
            p[j]=true;
            }
        }  
    }
    for(int i=0;i<count;i++){
        ans+=prime[i];
    }
    cout<<"100以内的素数和为："<<ans<<endl;  
}

int main(){
    findprime();
    return 0;
}