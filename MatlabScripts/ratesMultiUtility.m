% % % CHANGE NUMBER HERE % % %
numSimulations = 50;
numAgents = 1000;
numRounds = 500;
tax = [num2str(25) '%'];
e1 = ['e = ' num2str(0.25)];
e2 = ['e = ' num2str(0.75)];

% % % String Variables % % %
s2 = num2str(numSimulations);
s3 = ' Simulations (';
s4 = num2str(numAgents);
s5 = ' Players, ';
s6 = num2str(numRounds);
s7 = ' Rounds)';
s1 = [s2 s3 s4 s5 s6 s7];

% % % Rate Convergence Graph % % % 
sum1 = 0;
sum2 = 0;
sum3 = 0;
h1 = 0;
i = 0;

for n=1:numSimulations
    rates = importdata(strcat('../../Desktop/BSEconomy/DataFiles/Rates/Rates',num2str(n),'.txt'));
    rates = sortrows(rates,3);
    h=size(rates,1);
    k = find(rates(1:h,3:3)==1,1,'last');
    plot(rates(1:k,2:2));
    hold on;
    if k<=20
        ans1=mean(rates(1:k,2:2));
    else
        ans1=mean(rates((k-20):k,2:2));
    end
    sum1 = sum1 + ans1;
    h1 = max(h1,k);
    
    k1 = find(rates(1:h,3:3)==2,1,'last');
    plot(rates(k+1:k1,2:2));
    hold on;
    if (k1-k)<=20
        ans2=mean(rates(k+1:k1,2:2));
    else
        ans2=mean(rates(k1-20:k1,2:2));
    end
    sum2 = sum2 + ans2;
    h1 = max(h1,(k1-k));
    
    plot(rates(k1+1:h,2:2));
    hold on;
    if (h-k1)<=20
        ans3=mean(rates(k1+1:h,2:2));
    else
        ans3=mean(rates(h-20:h,2:2));
    end
    sum3 = sum3 + ans3;
    h1 = max(h1,(h-k1));
end

ans12 = sum1/numSimulations;
ans22 = sum2/numSimulations;
ans32 = sum3/numSimulations;

xl=xlim;

if xl(1,2)-h1<10
    val=xl(1,2);
else 
    val=h1;
end

x = linspace(1,h1);
plot(x,ones(size(x))*ans12, 'LineWidth',3);
hold on;
plot(x,ones(size(x))*ans22, 'LineWidth',3);
hold on;
plot(x,ones(size(x))*ans32, 'LineWidth',3);

txt1 = num2str(ans12);
text(h1+50,ans12,txt1);
text(h1+50,ans12-0.15,e1);
txt1 = num2str(ans22);
text(h1+50,ans22,txt1);
text(h1+50,ans22-0.15,e2);
txt1 = num2str(ans32);
text(h1+50,ans32,txt1);

yl=ylim;

if yl(1,2)>5
    ylim([0,5]);
end

title(['Transaction Rate Convergence from ' s1]);
xlabel('Time');
ylabel('Transaction Rate (Cash/Wheat)');

savefig('../../Desktop/BSEconomy/PlotFiles/RatesMulti.fig');
print('../../Desktop/BSEconomy/Graphs/RatesMulti.png','-dpng');
close();