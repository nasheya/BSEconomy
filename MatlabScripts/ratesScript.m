% % % CHANGE NUMBER HERE % % %
numSimulations = 50;
numAgents = 1000;
numRounds = 500;
tax = [num2str(25) '%'];

% % % String Variables % % %
s2 = num2str(numSimulations);
s3 = ' Simulations (';
s4 = num2str(numAgents);
s5 = ' Players, ';
s6 = num2str(numRounds);
s7 = ' Rounds)';
s1 = [s2 s3 s4 s5 s6 s7];

% % % Rate Convergence Graph % % % 
sum = 0;
h1 = 0;
i = 0;

for n=1:numSimulations
    rates = importdata(strcat('../../Desktop/BSEconomy/DataFiles/Rates/Rates',num2str(n),'.txt'));
    h=size(rates,1);
    h1 = max(h1,h);
    if h>=1
        plot(rates(1:h, 1:1),rates(1:h,2:2));
        hold on;

        if h<=20
            ans1=mean(rates(1:h,2:2));
        else
            ans1=mean(rates((h-20):h, 2:2));
        end

        sum = sum + ans1;
    end
end

ans2 = sum/numSimulations;
xl=xlim;

if xl(1,2)-h1<10
    val=xl(1,2);
else 
    val=h1;
end

x = linspace(1,h1);
plot(x,ones(size(x))*ans2, 'LineWidth',5);
txt1 = num2str(ans2);
text(val+0.5,ans2,txt1);

yl=ylim;

if yl(1,2)>5
    ylim([0,5]);
end

title(['Transaction Rate Convergence from ' s1]);
xlabel('Time');
ylabel('Transaction Rate (Cash/Wheat)');

savefig('../../Desktop/BSEconomy/PlotFiles/Rates.fig');
print('../../Desktop/BSEconomy/Graphs/Rates.png','-dpng');
close();