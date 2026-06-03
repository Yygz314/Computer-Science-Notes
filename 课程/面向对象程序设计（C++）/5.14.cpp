#include<iostream>
using namespace std;
class area_c1{
	protected:
		double height;
		double width;
	public:
		area_c1(double r,double s){
			height=r;width=s;
		}
		virtual double area()=0;
};

class Rectangle:public area_c1{
	public:
		Rectangle(int a,int h);
		double area();
};

class Triangle:public area_c1{
	public:
		Triangle(int a,int h);
		double area();	
};

Rectangle::Rectangle(int a,int h):area_c1(a,h){ }
Triangle::Triangle(int a,int h):area_c1(a,h){ }
double Rectangle::area(){
	return height*width;
}
double Triangle::area(){
	return height*width/2;
}
int main(){
	double S1,S2,S3,S4;
	Rectangle s1(5.0,10.0),s3(6.0,4.0);
	Triangle s2(5.0,10.0),s4(6.0,4.0);
	S1=s1.area(),S2=s2.area(),S3=s3.area(),S4=s4.area();
	cout<<S1<<endl<<S2<<endl<<S3<<endl<<S4<<endl;
	return 0; 
}






