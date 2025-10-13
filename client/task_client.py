import tkinter
import requests

win_width = 600
win_height = 400
buttons_width = 200
things_height = 20
gap_x = 20
gap_y = 20
gap_top = 40

win = tkinter.Tk()
win.geometry(str(win_width) + "x" + str(win_height))

generate_frame = tkinter.Frame()
edit_frame = tkinter.Frame()

generate_frame.place(x=0, y=0, width=win_width//2, height=win_height)
edit_frame.place(x=win_width//2, y=0, width=win_width//2, height=win_height)

entry = tkinter.Entry(edit_frame)
entry.place(x=gap_x//2, y=gap_top+((gap_y+things_height)*2), width=(win_width//2)-(gap_x*3//2), height=things_height)

validate_button = tkinter.Button(edit_frame, text="Validate")
validate_button.place(x=gap_x//2, y=gap_top, width=buttons_width, height=things_height)

win.mainloop()



"""
response = requests.get('http://localhost:61399/uuid/generate')  
print(response.status_code)  
print(response.text)  
"""
