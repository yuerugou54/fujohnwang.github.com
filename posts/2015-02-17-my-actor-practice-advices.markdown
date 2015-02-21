% Practice Advices On Design and Impl. with Actor Model
% 王福强
% 2015.02.17

# Understand The Single-Direction-Message-Flow Essential Of Actor Model

# Seperate The Read And The Write(Single Responsibility Request Repository)

1. Never mix read and write request into one actor!
2. Reader Actor with OffHeap State as Query Service;
3. Writer Actor as Computation unit or processor with only transient states for internal use;



