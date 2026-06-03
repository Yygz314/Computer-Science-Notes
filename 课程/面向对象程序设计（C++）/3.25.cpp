/*
 * @LastEditors  : Yygz314 2711859393@qq.com
 * @LastEditTime : 2025-03-28 13:28:30
 * @Author       : Yygz314
 * @Date         : 2025-03-28 13:07:19
 * @blog         : https://www.cnblogs.com/Yygz314
 */
#include<iostream>
using namespace std;
class cylinder{
    private:
        double radius;
        double height;
        double V;
    public:
        cylinder(double r,double h);
        void setcylinder(double r,double h){
            radius=r;
            height=h;
        }
        double getVol(){
            V=3.14*radius*radius*height;
            return V;
        }
        void vol(){
            V=getVol();
            cout<<V<<endl;
        }
};
cylinder::cylinder(double r,double h){
    radius=r;
    height=h;
}
int main(){
    //ÒÔ°ë¾¶1.5£¬¸ß2.0¾ÙÀý
    cylinder c(1.5,2.0);
    c.vol();
    return 0;
}