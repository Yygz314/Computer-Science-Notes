/*
 * @LastEditors  : Yygz314 2711859393@qq.com
 * @LastEditTime : 2025-03-21 11:06:28
 * @Author       : Yygz314
 * @Date         : 2025-03-21 11:02:03
 * @blog         : https://www.cnblogs.com/Yygz314
 */
#include<iostream>
using namespace std;
class counter{
    private:
        int value;
    public:
        counter(int number);
        void increament();
        void decrement();
        int getvalue();
        int print();
};
counter::counter(int number){
    value=number;
}
void counter::increament(){
    value++; 
}
void counter::decrement(){
    value--; 
}
int counter::getvalue(){
    return value;
}
int counter::print(){
    cout<<"value: "<<value<<endl; 
}
int main(){
    counter op1(10);
    op1.print();
    op1.increament();
    op1.print();
    op1.decrement();
    op1.print();
    cout<<"value: "<<op1.getvalue()<<endl;
    return 0; 
}