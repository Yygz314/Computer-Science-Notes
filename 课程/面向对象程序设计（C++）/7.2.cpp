#include<iostream>
using namespace std;

class Count{
	private:
		double a,b;
	public:
		Count(double x,double y);
		void add(double a,double b){
			cout<<a+b<<endl;
		}
		void low(double a,double b){
			cout<<a-b<<endl;
		}
		void mtp(double a,double b){
			cout<<a*b<<endl;
		}
		void div(double a,double b){
			if(b==0) cout<<"除数为0，错误运算"<<endl;
			else cout<<a/b<<endl;
		}	
};

Count::Count(double x,double y){
	a=x;
	b=y;
}

int main(){
	double a,b,flag=1;
	while(flag==1){
		cout<<"请输入两个数："<<endl;
		cout<<"a:";	cin>>a;
		cout<<"b:";	cin>>b;
		cout << "\n请选择运算：\n"
        	 << "1: 加法\n"
         	<< "2: 减法\n"
         	<< "3: 乘法\n"
         	<< "4: 除法\n"
         	<< "请输入选项(1-4): ";
        Count ct(a,b);
        int choice;
        cin>>choice;
        if(choice<1||choice>4) cout<<"无效选项"<<endl;
		else{
			if(choice==1) ct.add(a,b);
			else if(choice==2) ct.low(a,b);
			else if(choice==3) ct.mtp(a,b);
			else ct.div(a,b);
		} 
		cout<<"是否继续（1：继续；0：停止）：";
		cin>>flag; 
	}
	return 0;
}
