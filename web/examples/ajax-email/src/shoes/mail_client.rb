require 'rubygems'
require 'erb'
require 'net/http'  
require 'enumerator'  
require 'ping'

class Navigation
  def initialize(slot)
    @view = slot  
    render
  end
  
  def update_inbox_count(count)
     @view.app{
       @inbox_label.replace "Inbox #{count}" 
     }
  end   
  
  def update_total_mail_count(count)
      @view.app{
         @mail_label.replace "Mail #{count}" 
       }
  end
  
  private
  def render
     @view.append {  
       @view.app {  
         stack :margin_left => 5, :margin_right => 5 do
           stack do  
              @mail_label = para 'Mail 0', :stroke => '#222', :size => 'x-small'
            end
         
           stack do  
             background white
             border red , :curve => 1
             @inbox_label = para 'Inbox 0', :stroke => '#222', :size => 'x-small'
           end
           
           stack do  
             @sent_label = para 'Sent 0', :stroke => '#222', :size => 'x-small'
           end  
         end
       }
     }
  end
end

class InboxView
  def initialize(slot)
    @view = slot
    @message_view = {}
    @messages = {}
  end    
  
  attr_accessor :message_view, :messages
   
   def render_message(message)  
    id = message[:id] 
    @view.app {  
      view = @inbox_view.message_view
      
      view[id] = para '' , :size => 'medium'   if view[id].nil?
      elem = message[:read] ? message[:subject] : strong(message[:subject]) 
      view[id].replace link(elem)    
    }
  end
    
  def add_messages(messages)   
    messages.each_with_index do |message,index| 
      @messages[message[:id]] = message  
      @view.append { @view.app do 
        style(Link, :stroke => black, :underline => nil, :weight => "strong")
        style(LinkHover, :stroke => black, :fill => nil, :underline => nil)
        
        stack :width => 1.0 do
          background "#fff".."#eed"
          hi = background "#ddd".."#ba9", :hidden => true   
          # alert m.inspect
          @inbox_view.render_message(message)
          hover { hi.show }
          leave { hi.hide }
          click { @inbox_view.open_mail message[:id] }
        end
        # para i.to_s + " : "+ m[:subject] + "\n", :stroke => green 
      end
      }   
      
       # @message_view = stack do
       #            border gray, :strokewidth => 0.5
       #            inscription  index
       #            # para message.body , :weight => 'light' , :stroke => yellow
       #            para "message #{message}" , :weight => 'light' , :stroke => blue
       #        end
    end
  end  
  
  def open_mail  id
     # message = @messages[id]
     # Shoes.p "message is #{message}" 
     @view.app { 
       message = @inbox_view.messages[id]
       Shoes.p "m is #{message}"
       window do
          message.each do |k,v|
            para "#{k} : #{v} \n"
          end
       end  
     }
     refresh(@messages[id])
  end
       
  def refresh(message) 
    render_message(message)
    # @message_view[message[:id]] = render_message(message)
  end
  # def add_message(message)
  #    
  #  end
  #  
end


Shoes.app{
  #login success or exit with dialog
  #get classes from server and instantiate
  SERVER_URI='http://ph-jdo.******.com/ruby'
  CLASS_LIST='Auth,Mailbox,Inbox,Sent,Connection,RubyParser,JSONParser'
  URL = "http://ph-jdo.******.com/ruby/classdefs?#{CLASS_LIST}"
  
  @binding = binding
  
  stack :margin => 10, :margin_top => 10 do   
    background "#C7EAFB"  
     # dl = download URL, :save => File.basename('classes.rb'), :finish => proc { |dl| finished = 'finished' }
        
     # count = 10
     #     if(finished.nil? &&  count > 0)
     #       sleep 1;
     #       count = count - 1
     #     end       
    
      # require "#{File.dirname(__FILE__)}/classes.rb"
      # require "#{File.dirname(__FILE__)}/classes_modified.rb"
      #load "#{File.dirname(__FILE__)}/classes_modified.rb"
      
#      @connection  = Connection.new 
#      @auth = Auth.new(@connection)    
      @headers = nil    
      @inbox_view = nil  
      @navigation = nil
      @status = nil
      
      def logIn
        Shoes.p "logging in"  
        @args = {}
        @args['userName'] =  'Gill Bates'
        @args['password'] =  '1234'           
      
        body = @args.map {|k,v| "#{k}=#{ERB::Util.url_encode(v)}"}.join("&")   
      
        download "#{SERVER_URI}/Auth/logIn",:method => "POST", :body => body, :headers => @headers do  |resp|
            response = resp.response 
            Shoes.debug response.headers.inspect
            Shoes.p "auth...data is #{response.body}"
            @headers = { 'Cookie' => response.headers['Set-Cookie']} if response.headers['Set-Cookie']
        end
      
        Shoes.p 'authentication...'
        Shoes.debug @headers 
      end   
      
      logIn  
      #Trying to block for download thread 
      sleep 2
      
      if(@headers.nil?)
        #We are doomed here...
        Shoes.error 'invalid headers'
        # logIn
      end
      
     flow :margin_right => 1 do  
       border "#00D0FF", :strokewidth => 3, :curve => 5
       stack :margin_left => 1, :margin_right => 1, :margin_top => 5, :width => 1.0 do
          elem = flow do
            para "Mail for #{@args['userName']}" , :stroke => black  
            @status = inscription  '',  :align => 'right'   
          end      
          stack :width => 1.0 do
              border  black
           end
       end    
       
       flow :width => 1.0, :margin_bottom => 5 do
         flow :margin_left => 1, :width => "19%" do
            @navigation = Navigation.new(stack)
         end  

          flow :width => '1%' do
            background black
          end   
         
          view = flow :margin_right => 1, :width => "80%" do
              @status.replace 'loading messages'  
              sleep 0.5
          end
          
          @inbox_view  = InboxView.new(view)
       end 
     end  
     
     
     download "#{SERVER_URI}/Inbox/messages", :headers => @headers ,:finish => proc{ |resp|
        response = resp.response 
        # Shoes.debug response.headers.inspect
        # Shoes.debug response.body       
        @headers = { 'Cookie' => response.headers['Set-Cookie']} if response.headers['Set-Cookie']  
        @data = response.body
        inbox_messages = eval(response.body)
        Shoes.p "inbox message  length is #{inbox_messages.length}"
        # Shoes.p "inbox message is #{inbox_messages[0]}"

        @status.replace 'adding messages'     
        
        sleep 0.5
        @inbox_view.add_messages(inbox_messages) 
        sleep 0.5
        @status.replace 'updateing count'   
        @navigation.update_total_mail_count inbox_messages.length
        @navigation.update_inbox_count inbox_messages.reject{|m| m[:read] == true}.length
        sleep 0.5
        @status.replace ''
      }                   
      end
          #   
          # def open_mail id
          #   alert "path is #{SERVER_URI}/Inbox/read/#{id}"
          #   
          #   download 'http://google.com', :finish => proc { |resp| para resp.response.body }
          #           #  
          #           # download "#{SERVER_URI}/Inbox/read/#{id}", :headers => @headers ,:finish => proc{ |resp|
          #           #   response = resp.response 
          #           #   # Shoes.debug response.body       
          #           #   # @headers = { 'Cookie' => response.headers['Set-Cookie']} if response.headers['Set-Cookie']  
          #           #   message = eval(response.body)
          #           #   para "message in widow is #{message}" 
          #           #   # @message_window.replace  message , :stroke => '#A0A0A0'
          #           # }                   
          #           #                           
          #    
          #   download "#{SERVER_URI}/Inbox/read/#{id}", :headers => @headers ,:finish => proc{ |resp|
          #     response = resp.response 
          #     # Shoes.debug response.body       
          #     # @headers = { 'Cookie' => response.headers['Set-Cookie']} if response.headers['Set-Cookie']  
          #     message = eval(response.body)
          #     para "message in widow is #{message}" 
          #     # @message_window.replace  message , :stroke => '#A0A0A0'
          #   }                   
          #  
          # end    
}

