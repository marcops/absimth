#include "../library/stdio.hpp"
#include "../library/math.hpp"

float Normal(float zz) { 
    //cdf of 0 is 0.5
    if (zz == 0)   
    { 
        return 0.5;
    }
    
    float z = zz;  //zz is input variable,  use z for calculations
    
    if (zz < 0)
        z = -zz;  //change negative values to positive
    
    //set constants
    float p = 0.2316419;  
    float b1 = 0.31938153;
    float b2 = -0.356563782;
    float b3 = 1.781477937;
    float b4 = -1.821255978;
    float b5 = 1.330274428;
    
    //CALCULATIONS
    float f = 1 / native_sqrtf(2 * M_PI);
    float ff = exp(-pow(z, 2) / 2) * f;
    float s1 = b1 / (1 + p * z);
    float s2 = b2 / pow((1 + p * z), 2);
    float s3 = b3 / pow((1 + p * z), 3);
    float s4 = b4 / pow((1 + p * z), 4);
    float s5 = b5 / pow((1 + p * z), 5);


    //sz is the right-tail approximation
    float  sz = ff * (s1 + s2 + s3 + s4 + s5); 
        
    float rz; 
    //cdf of negative input is right-tail of input's absolute value 
    if (zz < 0)
        rz = sz;
    
    //cdf of positive input is one minus right-tail 
    if (zz > 0)
        rz = (1 - sz);
    
    return rz;
}
float callValue(float strike, float s, float sd, float r, float days)
{ 
     float ls = log(s);
     float lx = log(strike);
     float t = days / 365;
     float sd2 = pow(sd, 2);
     float n = (ls - lx + r * t + sd2 * t / 2);
     float native_sqrtT = native_sqrtf(days / 365);
     float d = sd * native_sqrtT;
     float d1 = n / d;
     float d2 = d1 - sd * native_sqrtT;
     float nd1 = Normal(d1);
     float nd2 = Normal(d2);
     return s * nd1 - strike * exp(-r * t) * nd2;
}
    
float putValue(float strike, float s, float sd, float r, float days)
{
     float ls = log(s);
     float lx = log(strike);
     float t = days / 365;
     float sd2 = pow(sd, 2);
     float n = (ls - lx + r * t + sd2 * t / 2);
     float native_sqrtT = native_sqrtf(days / 365);
     float d = sd * native_sqrtT;
     float d1 = n / d;
     float d2 = d1 - sd * native_sqrtT;
     float nd1 = Normal(d1);
     float nd2 = Normal(d2);
     return strike * exp(-r * t) * (1 - nd2) - s * (1 - nd1);
}

int main(int argc, char *argv[])
{
    float strike_price = 40.3;
    float asset_price = 50;
    float days_to_exp = 5;
    float risk_free_rate = 3.66;
    float standard_deviation = 62;

    //  float strike_price = atof(argv[1]);
    //  float asset_price = atof(argv[2]);
    //  float standard_deviation = atof(argv[3]);
    //  float risk_free_rate = atof(argv[4]);
    //  float days_to_exp = atof(argv[5]);
    // print_str("Strike Price:");
    // print_float(strike_price, 2);

    //  printf("Strike Price: %f \n", strike_price);
    //  printf("Asset Price:  %f \n", asset_price);
    //  printf("Std Dev:      %f \n", standard_deviation);
    //  printf("Risk Free:    %f \n", risk_free_rate);
    //  printf("Days to Exp:  %f \n", days_to_exp);
    print_float(putValue(strike_price, asset_price, standard_deviation, risk_free_rate, days_to_exp));
    print_float(callValue(strike_price, asset_price, standard_deviation, risk_free_rate, days_to_exp));
    //print_float(Normal(-3.66));
    return 0;
}
