#include<iostream>
#include<cstring>
using namespace std;

class Stock{
	private:
		char stockcode[100];
		int quantity=100;
		double price=8.98;
	public:
		Stock(char na[100],int q,double p){
			strcpy(stockcode,na);
			quantity=q;
			price=p;
		}
		void print(){
			cout<<this->stockcode<<endl<<this->quantity<<endl<<this->price<<endl;
		}
};

int main(){
	char na[100];
	int q=100;
	double p=8.98;
	cin>>na>>q>>p;
	Stock s(na,q,p);
	s.print();
	return 0;
}
