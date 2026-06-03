#include<iostream>
#include<iomanip>
using namespace std;

class Toy{
	private:
		int price;
		int count;
		int sum;
	public:
		Toy(double p,int c){
			price=p;
			count=c;
			sum=p*c;
		}
		void showsum(){
			cout<<"Price="<<price<<",Count="<<count<<",Total="<<sum<<endl;
		}
};

int main(){
	int m,n;
	while(cin>>m>>n){
		Toy list(m,n);
		list.showsum();
	}
	return 0;
} 
