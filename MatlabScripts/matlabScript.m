% % % CHANGE NUMBER HERE % % %
n=1000;

% % % Total Utility Graph % % % 
au = importdata('../../Desktop/BSEconomy/DataFiles/AfterUtility.txt');
bu = importdata('../../Desktop/BSEconomy/DataFiles/BeforeUtility.txt');
histogram(bu,50);
hold on;
histogram(au,100);
title('Before and After Utility Distribution of 50 Simulations (1000 Players, 1000 Rounds)');
xlabel('Total Utility');
ylabel('Number of Players');
legend('Before','After');
savefig('../../Desktop/BSEconomy/PlotFiles/Utility.fig');
print('../../Desktop/BSEconomy/Graphs/Utility.png','-dpng');
close();

% % % Total Amount Graph % % % 
ta = importdata('../../Desktop/BSEconomy/DataFiles/TotalAmount.txt');
histogram(ta,100);
title('Total Amount Distribution of 50 Simulations (1000 Players, 1000 Rounds)');
xlabel('Total Amount');
ylabel('Number of Players');
savefig('../../Desktop/BSEconomy/PlotFiles/Amount.fig');
print('../../Desktop/BSEconomy/Graphs/Amount.png','-dpng');
close();

% % % Cash Wheat Graph % % % 
a = importdata('../../Desktop/BSEconomy/DataFiles/AfterCashWheat.txt');
b = importdata('../../Desktop/BSEconomy/DataFiles/BeforeCashWheat.txt');
plot(b(1:n*50,1:1),b(1:n*50,2:2));
hold on;
plot(a(1:n*50,1:1),a(1:n*50,2:2),'.');
xlabel('Cash');
ylabel('Wheat');
title('Cash and Wheat Distribution of 50 Simulations (1000 Players, 1000 Rounds)');
legend('Before','After');
savefig('../../Desktop/BSEconomy/PlotFiles/CashWheat.fig');
print('../../Desktop/BSEconomy/Graphs/CashWheat.png','-dpng');
close();

% % % % Government Graph % % %
% govt = importdata('../../Desktop/BSEconomy/DataFiles/Govt.txt');
% plot(govt(1:50,1:1),govt(1:50,2:2), '.');
% hold on;
% xlabel('Cash');
% ylabel('Wheat');
% title('Government Tax Distribution of 50 Simulations with a 25% Tax (10 Players, 500 Rounds)');
% savefig('../../Desktop/BSEconomy/PlotFiles/Govt.fig');
% print('../../Desktop/BSEconomy/Graphs/Govt.png','-dpng');
% close();