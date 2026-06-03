#include<iostream>
using namespace std;
class B1{
	public:
		B1(int i){
			b1=i;cout<<"Constructor B1. "<<endl;
		}
		void Print(){
			cout<<b1<<endl;
		}
	private:
		int b1; 
}; 
class B2{
	public:
		B2(int i){
			b2=i;cout<<"Constructor B2. "<<endl;
		}
		void Print(){
			cout<<b2<<endl;
		}
	private:
		int b2;
};
class A:public B2,public B1{
	public:
		A(int i,int j,int l);
		void Print();
	private:
		int a;
};
A::A(int i,int j,int l):B1(i),B2(j){
	a=l;cout<<"Constructor A. "<<endl;
}
void A::Print(){
	B1::Print();
	B2::Print();
	cout<<a<<endl;
}
int main(){
	A aa(3,2,1);
	aa.Print();
	return 0;
}

//OUTPUT:
/*
Constructor B2. 
Constructor B1. 
Constructor A.
3
2
1
*/






