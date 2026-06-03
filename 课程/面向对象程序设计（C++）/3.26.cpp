/*
 * @LastEditors  : Yygz314 2711859393@qq.com
 * @LastEditTime : 2025-03-28 13:41:50
 * @Author       : Yygz314
 * @Date         : 2025-03-28 13:29:01
 * @blog         : https://www.cnblogs.com/Yygz314
 */
#include<iostream>
using namespace std;
class Stock{
    private:
        string stockcode;
        int quantity;
        double price;
    public:
        Stock(string sc = "default", int q = 1000, double p = 8.98): stockcode(sc), quantity(q), price(p) {}
        void setStock(string code,int q,double p){
            stockcode=code;
            quantity=q;
            price=p;
        }
        void print(){
            cout<<this->stockcode<<" "<<this->quantity<<" "<<this->price<<endl;
        }
};

int main(){
    Stock s1("600001",3000,5.67);
    s1.print();
    Stock s2("600001");
    s2.print();
    return 0;
}