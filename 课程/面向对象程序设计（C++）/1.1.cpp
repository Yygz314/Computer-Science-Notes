/*
 * @Author: Yygz314 2711859393@qq.com
 * @Date: 2025-02-21 13:22:41
 * @LastEditors: Yygz314 2711859393@qq.com
 * @LastEditTime: 2025-02-21 13:35:48
 * @FilePath: \VScode\homework_cpp\1.1.cpp
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
#include<bits/stdc++.h>
using namespace std;

//冒泡排序
void maopao(int arr[],int len){
    for(int i=0;i<len-1;i++){
        for(int j=0;j<len-i-1;j++){
            if(arr[j]>arr[j+1]){
                int temp=arr[j];
                arr[j]=arr[j+1];
                arr[j+1]=temp;
            }
        }
    }
    cout<<"冒泡排序的结果： ";
    for(int i=0;i<len;i++){
        cout<<arr[i]<<" ";
    }
    cout<<endl;
}

//选择排序
void xuanze(int arr[],int len){
    for(int i=0;i<len-1;i++){
        int min=i;
        for(int j=i+1;j<len;j++){
            if(arr[j]<arr[min]){
                min=j;
            }
            if(min!=i){
                int temp=arr[i];
                arr[i]=arr[min];
                arr[min]=temp;
            }
        }
    }
    cout<<"选择排序的结果： ";
    for(int i=0;i<len;i++){
        cout<<arr[i]<<" ";
    }
    cout<<endl;
}

int main(){
    int arr[100];
    int len=0;
    for(int i=0;i<10;i++){
        cin>>arr[i];
        len++;
    }
    maopao(arr,len);
    xuanze(arr,len);
    return 0;
}