% URL Encoding And Escaping
% [陨石 - yunshi@wacai.com](mailto:yunshi@wacai.com)
% 2015-02-04 

<pre>
URLs can only be sent over the Internet using the ASCII character-set.

Since URLs often contain characters outside the ASCII set, the URL has to be converted into a valid ASCII format.

URL encoding replaces unsafe ASCII characters with a "%" followed by two hexadecimal digits.

URLs cannot contain spaces. URL encoding normally replaces a space with a plus (+) sign or with %20.
</pre>

<tr>
  <td class="frame" align="right">
  <table class="data">
   <tbody><tr>
    <th class="data">
     Character
    </th>
    <th class="data">
     Escape Code
    </th>
   </tr>
   <tr>
    <td class="data" align="center">
     <font size="+1">SPACE</font> 
    </td>
    <td class="data" align="center">
     <font size="+1">%20</font>
    </td>
   </tr>
   <tr>
    <td class="data" align="center">
     <font size="+1">&lt;</font> 
    </td>
    <td class="data" align="center">
     <font size="+1">%3C</font>
    </td>
   </tr>
   <tr>
    <td class="data" align="center">
     <font size="+1">&gt;</font> 
    </td>
    <td class="data" align="center">
     <font size="+1">%3E</font>
    </td>
   </tr>
   <tr>
    <td class="data" align="center">
     <font size="+1">#</font> 
    </td>
    <td class="data" align="center">
     <font size="+1">%23</font>
    </td>
   </tr>
   <tr>
    <td class="data" align="center">
     <font size="+1">%</font> 
    </td>
    <td class="data" align="center">
     <font size="+1">%25</font>
    </td>
   </tr>
   <tr>
    <td class="data" align="center">
     <font size="+1">{</font> 
    </td>
    <td class="data" align="center">
     <font size="+1">%7B</font>
    </td>
   </tr>
   <tr>
    <td class="data" align="center">
     <font size="+1">}</font> 
    </td>
    <td class="data" align="center">
     <font size="+1">%7D</font>
    </td>
   </tr>
   <tr>
    <td class="data" align="center">
     <font size="+1">|</font> 
    </td>
    <td class="data" align="center">
     <font size="+1">%7C</font>
    </td>
   </tr>
   <tr>
    <td class="data" align="center">
     <font size="+1">\</font> 
    </td>
    <td class="data" align="center">
     <font size="+1">%5C</font>
    </td>
   </tr>
   <tr>
    <td class="data" align="center">
     <font size="+1">^</font> 
    </td>
    <td class="data" align="center">
     <font size="+1">%5E</font>
    </td>
   </tr>
   <tr>
    <td class="data" align="center">
     <font size="+1">~</font> 
    </td>
    <td class="data" align="center">
     <font size="+1">%7E</font>
    </td>
   </tr>
   </tbody></table>
  </td>
  <td class="frame" align="left">
  <table class="data">
   <tbody><tr>
    <th class="data">
     Character
    </th>
    <th class="data">
     Escape Code
    </th>
   </tr>
   <tr>
    <td class="data" align="center">
     <font size="+1">[</font> 
    </td>
    <td class="data" align="center">
     <font size="+1">%5B</font>
    </td>
   </tr>
   <tr>
    <td class="data" align="center">
     <font size="+1">]</font> 
    </td>
    <td class="data" align="center">
     <font size="+1">%5D</font>
    </td>
   </tr>
   <tr>
    <td class="data" align="center">
     <font size="+1">`</font> 
    </td>
    <td class="data" align="center">
     <font size="+1">%60</font>
    </td>
   </tr>
   <tr>
    <td class="data" align="center">
     <font size="+1">;</font> 
    </td>
    <td class="data" align="center">
     <font size="+1">%3B</font>
    </td>
   </tr>
   <tr>
    <td class="data" align="center">
     <font size="+1">/</font> 
    </td>
    <td class="data" align="center">
     <font size="+1">%2F</font>
    </td>
   </tr>
   <tr>
    <td class="data" align="center">
     <font size="+1">?</font> 
    </td>
    <td class="data" align="center">
     <font size="+1">%3F</font>
    </td>
   </tr>
   <tr>
    <td class="data" align="center">
     <font size="+1">:</font> 
    </td>
    <td class="data" align="center">
     <font size="+1">%3A</font>
    </td>
   </tr>
   <tr>
    <td class="data" align="center">
     <font size="+1">@</font> 
    </td>
    <td class="data" align="center">
     <font size="+1">%40</font>
    </td>
   </tr>
   <tr>
    <td class="data" align="center">
     <font size="+1">=</font> 
    </td>
    <td class="data" align="center">
     <font size="+1">%3D</font>
    </td>
   </tr>
   <tr>
    <td class="data" align="center">
     <font size="+1">&amp;</font> 
    </td>
    <td class="data" align="center">
     <font size="+1">%26</font>
    </td>
   </tr>
   <tr>
    <td class="data" align="center">
     <font size="+1">$</font> 
    </td>
    <td class="data" align="center">
     <font size="+1">%24</font>
    </td>
   </tr>
  </tbody></table>
    </td>
   </tr>