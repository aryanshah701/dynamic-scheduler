import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConstantPQ {
  static int MAX_MINUTE = 24 * 60;
  private int[][] requests;
  private int[] finishes;
  private int count;

  public ConstantPQ() {
    requests = new int[1440][1440];
    finishes = new int[1440];
    count = 0;
  }

  public static void main(String[] args) {
    //All unprocessed requests in a constant time priority queue
    ConstantPQ requests = new ConstantPQ();

    //Processed and executed requests
    List<Request> opt = new ArrayList<>();

    //Elapsed time
    int elapsedDayMinute = 0;

    //Scanner to get input from stdin
    Scanner sc = new Scanner(System.in);

    //Parsing input request
    while (sc.hasNext()) {
      String in = sc.nextLine();

      //If it is a 24-hour time
      if (!in.contains(",")) {
        //Converting 24 hour time to minutes
        int dayMinute = Request.toMinutes(in);

        //Printing and processing all unprocessed requests up till this dayMinute
        //Processed requests are removed from requests and added to opt
        processRequests(requests, opt, dayMinute, elapsedDayMinute);

        //Updating elapsed time
        elapsedDayMinute = dayMinute;

      } else {

        //If it is add/remove request then resort requests
        if (in.contains("cancel")) {
          //Removing request
          String stringRequest = in.split(" ")[1];
          Request requestToRemove = new Request(stringRequest);
          requests.remove(requestToRemove);

        } else {
          //Adding a request
          Request requestToAdd = new Request(in);
          requests.add(requestToAdd);
        }
      }
    }
  }

  //Prints the non-overlapping requests upto the given dayMinute from the sorted list of requests
  //Also removes all requests before the elapsedDayMinute since they're time to be executed has
  //passed.
  private static void processRequests(ConstantPQ requests, List<Request> opt,
      int dayMinute, int elapsedDayMinute) {
    //Removing unprocessed requests with start times <= elapsedDayMinute till a request with
    //dayMinute > elapsedDayMinute is reached
    while (requests.size() != 0) {
      Request request = requests.peek();

      if (request.startMinute <= elapsedDayMinute) {
        //Removing scheduled requests that didn't make it in time
        requests.poll();

      } else if (request.startMinute <= dayMinute) {
        //Scheduling request if it can be scheduled without overlapping with last optimal request
        if (opt.isEmpty() || !request.overlaps(opt.get(opt.size() - 1))) {
          System.out.println(request);
          opt.add(request);
        }

        //Removing scheduled request from unprocessed requests
        requests.poll();
      } else {
        //Optimal request to be schedule is after this dayMinute and hence nothing needs to be done
        break;
      }
    }
  }

  public Request poll() {
    //Getting the top request
    Request topRequest = this.peek();

    //Removing top request
    this.remove(topRequest);

    return topRequest;
  }

  public Request peek() {
    if (this.count == 0) {
      throw new IllegalArgumentException("No requests to peek/poll");
    }

    //Figuring out the minimum end time from all requests
    int minFinishTime = 0;
    for (int i = 0; i < this.finishes.length; i++) {
      if (this.finishes[i] > 0) {
        minFinishTime = i;
        break;
      }
    }

    //Figuring out the request itself(preference given to later start time)
    int startTime = 0;
    for (int i = this.requests.length - 1; i >= 0; i--) {
      if (this.requests[i][minFinishTime] > 0) {
        startTime = i;
        break;
      }
    }

    return new Request(startTime, minFinishTime);
  }

  public void add(Request request) {
    this.requests[request.startMinute][request.endMinute] += 1;
    this.finishes[request.endMinute] += 1;
    this.count++;
  }

  public void remove(Request request) {
    if (this.requests[request.startMinute][request.endMinute] == 0) {
      throw new IllegalArgumentException("Attempting to remove a non existing request " + request);
    }

    this.requests[request.startMinute][request.endMinute] -= 1;
    this.finishes[request.endMinute] -= 1;
    this.count--;
  }

  public int size() {
    return this.count;
  }

  // Here's a handy class for requests that implements some tricky bits for
  // you, including a compareTo method (so that it's a Comparable, so that
  // it can be sorted with Collections.sort()), a hashCode method
  // (so that identical time ranges are treated as identical keys in
  // a hashMap), and an overlaps() method (which students have often
  // gotten wrong in the past by omitting cases).  Parsing, equals(), and
  // toString() are also handled for you.
  public static class Request implements Comparable {

    private int startMinute;
    private int endMinute;

    // Constructor that takes the request format specified in the
    // assignment (startTime,endTime using 24-hr clock)
    public Request(String inputLine) {
      String[] inputParts = inputLine.split(",");
      startMinute = toMinutes(inputParts[0]);
      endMinute = toMinutes(inputParts[1]);
    }

    public Request(int startMinute, int endMinute) {
      if (startMinute < 0 || endMinute < 0) {
        throw new IllegalArgumentException("Illegal start/end minute");
      }

      this.startMinute = startMinute;
      this.endMinute = endMinute;
    }

    // Convert time to an integer number of minutes; mostly
    // for internal use by the class
    private static int toMinutes(String time) {
      String[] timeParts = time.split(":");
      int hour = Integer.valueOf(timeParts[0]);
      int minute = Integer.valueOf(timeParts[1]);
      return hour * 60 + minute;
    }

    // Don't feel like you need to use these accesssors, but they're
    // here in case I decide to change the internal representation
    // someday
    public int getStartMinute() {
      return startMinute;
    }

    public int getEndMinute() {
      return endMinute;
    }

    // Did you know toString() gets called automatically when your object
    // is put in a situation that expects a String?
    public String toString() {
      return timeToString(startMinute) + "," + timeToString(endMinute);
    }

    // Mostly for use by toString() - format number of minutes as 24hr time
    private static String timeToString(int minutes) {
      if ((minutes % 60) < 10) {
        return (minutes / 60) + ":0" + (minutes % 60);
      }
      return (minutes / 60) + ":" + (minutes % 60);
    }

    // Check whether two Requests overlap in time.
    public boolean overlaps(Request r) {
      // Four kinds of overlap...
      // r starts during this request:
      if (r.getStartMinute() >= getStartMinute() &&
          r.getStartMinute() < getEndMinute()) {
        return true;
      }
      // r ends during this request:
      if (r.getEndMinute() > getStartMinute() &&
          r.getEndMinute() < getEndMinute()) {
        return true;
      }
      // r contains this request:
      if (r.getStartMinute() <= getStartMinute() &&
          r.getEndMinute() >= getEndMinute()) {
        return true;
      }
      // this request contains r:
      if (r.getStartMinute() >= getStartMinute() &&
          r.getEndMinute() <= getEndMinute()) {
        return true;
      }
      return false;
    }

    // Allows use of Collections.sort() on this object
    // (implements Comparable interface)
    public int compareTo(Object o) {
      if (!(o instanceof Request)) {
        throw new ClassCastException();
      }

      Request r = (Request) o;
      if (r.getEndMinute() > getEndMinute()) {
        return -1;
      } else if (r.getEndMinute() < getEndMinute()) {
        return 1;
      } else if (r.getStartMinute() < getStartMinute()) {
        // Prefer later start times, so sort these first
        return -1;
      } else if (r.getStartMinute() > getStartMinute()) {
        return 1;
      } else {
        return 0;
      }
    }

    // The hash function for the hashMap, without which our scheme
    // of counting requests with the same range would not work.
    // You don't need to call this yourself; it's used every time
    // get(), contains(), or something similar is called
    public int hashCode() {
      return MAX_MINUTE * startMinute + endMinute;
    }

    // Determine whether two objects are equal.  If we're not in a hashing
    // context, other generics will use this to implement functions like
    // contains() or remove().
    public boolean equals(Object o) {
      if (!(o instanceof Request)) {
        return false;
      }

      Request that = (Request) o;
      return (this.startMinute == that.startMinute && this.endMinute == that.endMinute);
    }
  }
}
