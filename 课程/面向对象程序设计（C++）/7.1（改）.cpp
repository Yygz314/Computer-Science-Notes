#include<iostream>
using namespace std;

class Price{
	private:
		const double PI=3.14159;
		const double width=3;
		const double Pr_zhal=35;
		const double Pr_guod=20;
		double R;
	public:
		Price(double r);
		double zhalan(double R){
			return 2*PI*R*Pr_zhal;
		}
		double guodao(double R){
			return PI*(2*R+width)*width*Pr_guod;
		}
		void solve(){
			cout<<zhalan(R+width)<<endl;
			cout<<guodao(R)<<endl;
		}
}; 

Price::Price(double r){
	R=r;
}

int main(){
	double r;
	cin>>r;
	Price p(r);
	p.solve();
	return 0;
} 
