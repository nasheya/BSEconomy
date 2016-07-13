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

% % % Total Utility Graph % % % 
au = importdata('../../Desktop/BSEconomy/DataFiles/AfterUtility.txt');
bu = importdata('../../Desktop/BSEconomy/DataFiles/BeforeUtility.txt');
histogram(bu,50);
hold on;
histogram(au,100);
title(['Before and After Utility Distribution of ' s1]);
xlabel('Total Utility');
ylabel('Number of Players');
legend('Before','After');
savefig('../../Desktop/BSEconomy/PlotFiles/Utility.fig');
print('../../Desktop/BSEconomy/Graphs/Utility.png','-dpng');
close();

% % % Total Amount Graph % % % 
ta = importdata('../../Desktop/BSEconomy/DataFiles/TotalAmount.txt');
histogram(ta,100);
title(['Total Amount Distribution of ' s1]);
xlabel('Total Amount');
ylabel('Number of Players');
savefig('../../Desktop/BSEconomy/PlotFiles/Amount.fig');
print('../../Desktop/BSEconomy/Graphs/Amount.png','-dpng');
close();

% % % Cash Wheat Graph % % % 
a = importdata('../../Desktop/BSEconomy/DataFiles/AfterCashWheat.txt');
b = importdata('../../Desktop/BSEconomy/DataFiles/BeforeCashWheat.txt');
plot(b(1:numAgents*numSimulations,1:1),b(1:numAgents*numSimulations,2:2));
hold on;
plot(a(1:numAgents*numSimulations,1:1),a(1:numAgents*numSimulations,2:2),'.');
xlabel('Cash');
ylabel('Wheat');
title(['Cash and Wheat Distribution of ' s1]);
legend('Before','After');
savefig('../../Desktop/BSEconomy/PlotFiles/CashWheat.fig');
print('../../Desktop/BSEconomy/Graphs/CashWheat.png','-dpng');
close();

% % % % Government Graph % % %
% govt = importdata('../../Desktop/BSEconomy/DataFiles/Govt.txt');
% plot(govt(1:numSimulations,1:1),govt(1:numSimulations,2:2), '.');
% hold on;
% xlabel('Cash');
% ylabel('Wheat');
% title(['Government Tax Distribution of ' s2 ' Simulations with a ' tax ' Tax (' s4 s5 s6 s7]);
% savefig('../../Desktop/BSEconomy/PlotFiles/Govt.fig');
% print('../../Desktop/BSEconomy/Graphs/Govt.png','-dpng');
% close();