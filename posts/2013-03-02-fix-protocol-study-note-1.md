% FIX Protocol Study Note - Part 1
% fujohnwang
% 2013-03-02
__author: fujohnwang__



# Big Picture
Institutional Investers <-> Brokerage Firms <-> Liquidation Sources

1. Institutional Investers
	- Mutual Funds
	- Hedge Fund
	- Asset Mgmt
2.  Brokerage Firms
	- Order Management System (OMS)
	- Order Routing System (ORS)
	- Execution System
	- Other Systems
3. Liquidation Sources
	- Exchange
	- ECN
	- Other sources

Each party will deploy a FIX Engine with one as client and one as server.

![a big picture](../images/fixprotocol/fix_protocol_parties.png)

# Structure of FIX Message

FIX Messages are composed of Field in a format of "Key=Value" and seperated by non-printed character such as ^. All of the Fields can be classified into 3 groups, that's:

1. Header Tags
2. Body Tags
3. Footer/Trailer Tags


# FIX Message Categories

1. Admin Messages
	- Logon
	- Logout
	- Heartbeat
	- Test Request
	- Resend Request
	- Reject
	- Sequence Reset/Gap Fill
2. Application Messages
	- Pre-Trade messages
		- IOIs, Quotes, News, Emails, MarketData, Security Info, etc.
	- Trade Messages
		- Single Orders, Basket/List Orders, Multi-leg Orders, Executions, Order Cancel, Cancel/Replace, Status, etc.
	- Post-Trade messages
		- Allocations, Settlement Instructions, Positions Mgmt, etc.

# References
1. [FIX Protocol Introduction, part 1](http://www.youtube.com/watch?v=HBtyQTVVfZ0)
2. [FIX Protocol Introduction, part 2](http://www.youtube.com/watch?v=vwyXySnGrbc)
3. [FIX Protocol Introduction, part 3](http://www.youtube.com/watch?v=8wx6TLECQBw)
4. [FIX Protocol Introduction, part 4](http://www.youtube.com/watch?v=1OseF27iMoc) 
