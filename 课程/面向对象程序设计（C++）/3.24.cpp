/*
 * @LastEditors  : Yygz314 2711859393@qq.com
 * @LastEditTime : 2025-03-21 11:17:45
 * @Author       : Yygz314
 * @Date         : 2025-03-21 11:08:01
 * @blog         : https://www.cnblogs.com/Yygz314
 */
#include<iostream>
using namespace std;
class Date{
    private:
        int month,day,year;
    public:
        void printDate();
        void setDay(int d);
        void setMonth(int m);
        void setYear(int y);
};

inline void Date::printDate(){
    cout<<"ÈÕÆÚ£º"<<year<<"/"<<month<<"/"<<day<<endl;
}

inline void Date::setDay(int d){
    day=d;
}

inline void Date::setMonth(int m){
    month=m;
}

inline void Date::setYear(int y){
    year=y; 
}

int main(){
    Date testDay;
    testDay.setDay(5);
    testDay.setMonth(10);
    testDay.setYear(2003);
    testDay.printDate();
    return 0;
}