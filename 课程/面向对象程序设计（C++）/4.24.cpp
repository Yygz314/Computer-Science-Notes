#include<iostream>
using namespace std;

class book{
	private:
		int qu;
		int price;
	public:
		book(int m,int n){
			qu=m;
			price=n;
		}
		void show(){
			cout<<qu*price<<endl;
		}
};

int main(){
	book elemet[5]={
		book(1,10),
		book(2,20),
		book(3,30),
		book(4,40),
		book(5,50)
	};
	for(int i=0;i<5;i++){
		elemet[i].show();
	}
	return 0;
} 
