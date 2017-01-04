package org.usfirst.frc.team5968.robot;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SPI;

public class NavXMXP {
	private AHRS ahrs;

	/**
	 * Yaw: Z axis </br>
	 * Pitch: X axis </br>
	 * Roll: Y axis </br>
	 * <img src=
	 * "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAOsAAACxCAIAAACqfd0TAAAACXBIWXMAAA
	 * 7DAAAOwwHHb6hkAAAAB3RJTUUH4AoYASYfSpkyFQAAAAZiS0dEAP8A/wD/oL2nkwAAOLdJREFUeNrtnQ
	 * dYFMf7x2fvIGDXqInlrz9RMTFGo6bYSzRqTKzYe8EuKBA7drFSRERREUR671V6E6T3pmCjWVAUFKTt/7
	 * 1Zbl2qdzQ52Hnm4Vlgb+9u9rPvfN933plBJFvYIsoFsU3AFpZgtrCFJZgtbGEJZgtLMFvYwhLMlnZcKi
	 * oqaj1mCWaLCJTy8nL6uKioiLXBbBFJfJOTkzWPH4998KBlzDBLMFuaUjzEhITojBp1uFevyIgI+LWsrI
	 * wlmC2iUUpKSszU1W/36hWIkMvcuYWFhdV0BUswW1p1MTpyxBihRwh5EoSXpmaLvS9LMFuaoAR5e9/p3D
	 * kdoWcImXbr5m5jwxLMFpHx3kpLS+8uXBgMBpjLTUTIcsCA4OBglmC2iIL3hh04LzMzy2++SUUoCSHg2H
	 * revI8fP7IEs6W1l7LSUviZmppqMGpULEIpHE4yQu4IWR050pIfgyWYLQ01wLhY7N8P1IICjiGIFCBYTM
	 * xOS4slmC2iUSKCgu58+y1o33iCiEMIqlWfPpEhISzBLWQ/2NKYBiwuLtZfsiQQIVDAUVgEhyOkP25cS4
	 * rgdm2DWY4bWbxMTS0wviCCYxACSxyGkOHixS38MdodwZmZmfHx8SzEjXzU8/Ly9CZMiEQoGeMbg22wF0
	 * Iup0+zBDdX63/69MnJ0lJ3/HjNwYO9XFxYiOsvUWFhVnp6IBWYrUQf2F2+7MwwwLEEASgbcbn33d1Zgp
	 * ue3fLycjd7+6tz5tyVkIhAKBoh7Z49HWxtWYhrba6srCzL06fN+va9hZDzzZs1T4BOzEBKKgHb3RgGwX
	 * d79YqJiWEJboKbQYUqqRISFKSzZIkuDranYLPxCFsObUlJKwMD6pwWSKFqtQUe7wr+18/JybHV0Lj900
	 * +OuK2A0esDByYlJVV7ifGmTX4IpeEIGkVwAnbjLKdPf/36NUtwY+/H534wKuq2vLxOr15+GNxUHPQBAx
	 * xNEA9xo+tISFjq6tZ8YbvClzooKirytrDQnTbNBvdRqdgzg1byRchg3TqmAfa1tzeRlAS+43BjUgTD+W
	 * 4I2R861PJfoe0QDK1M34/U1FSTY8e0Bgxw598JZnPHYIipMaTb4uIWGhrtE2Kaywe+vndXrDDhcsNxB5
	 * WEjWs0bjQwtKaAJl9LFBYWGs2Z8wCPwDHbE17lgJDd1asswY0tzzIybFRUNIcMsQeLi1s2ERsVZnNTNQ
	 * r8aDyMZARm5vBhCt/2IycofLOePzdVVNTr2vU+VgW0Z1b5nGMtAQe60tJpaWlwvq26uj22CDEMguNwRM
	 * K2a9cgvn/MEtzwYqqldZlvdxP4tyGmjgoQJxJEKjYzd5WUKL+7vRCMn9ggS8vbGNM0DGKtbQUtCTLMZN
	 * u2xMREi/HjeYEzLjeKcUISjgSbTJiQ/fw5S3BjS3hwsM733ydjAxxVN7tMM5OA+0rQfzc2bHjz5k07CV
	 * CU494mPijIctAgeNTjq6oCZvuAuc1AyPzbbw//8Ydv584PsQarZqfBhOsvW1ZQUMAS3ASqzvjECVusH6
	 * IYDR1TryVO4HDgfCeErs2f/+LFi/YAMaWXQBsYjB0bjc1qXQSDbX5IEGYI6eHOLb6qRxGDJUQAIL5vXy
	 * kjBMQS3KC7glswPj7+2pAhoOeSOBxBzDB1n8AIpSN0DyHtOXOys7PbPMQUwU+ePDGaMCG2boJjsMDwR+
	 * gqQQRhtyGqqlaOw2rNskOHcEfHr/JF2hTBdDDB5Phxe5zyFy0YwZUQEwR0lz5wtyZPTk5ObtsBCup75e
	 * fnW8rIhOOnPbpWiYVl7h3wE6gAcM1Gw1Vv2LB07OqJGMHluNR/QsPM2BevXL9pSUpK0vnpJ6HMcGWPSR
	 * DpuE+8MmZMeHh4G4aYvi/3Tp50x0ogtrY2ScEGWBOPByXXZhHisRk2mDw5MzNTNAiuwIX80vpCAq4+VO
	 * u/GtN307QZHT9uh9VwtGBq+LPjgiEOQUhNSsrb25v5YLTJ4mdiYtGxYwpmsSadIHwNEDLEKMfVYaS9EX
	 * JQVPxazzlqABx0efr0aVrdfUdJSQk8l7R3X+tjUBeyoM8eP37cMJSpVyUmJl6Xlga7kigmJriWiOEHjx
	 * /hCNGVAQM82noOkK+jo0XPnhTB1RoqCQcZNHE0La3ac84PA7/CM4uiHRx4hLR+T+758+fXrl07evSohY
	 * UFAA0mysHBITc3NyUlpebJeXl5hw8fdnJyqkkqdRAQEOBeRyqTHS6N/G56CgpOmMUY4Ws0joOCAtHu1c
	 * vJ1LQNByV8PT2t+vRJZdjgaL7AhaazxSI4HttaGlyg+SlCOXgExAGJbUeDLQ0dv9bnF45gY2PjiRMnnj
	 * 17VlZWVk1N7eXLl0VFRYaGhmvWrAHpSXH57t27hISEjx8/vnjxYvv27Xp6emCGP336RINL2XL4+e+///
	 * 76669wPvwK51B/hzMLCwuzcKGvBm/UgO8WFRWl3a8ftH5SjQCQgBCn4oPrHTva1Zai1TYKdFZWP/wQx7
	 * fB8fzxoJcIvcES4i7ixcupvwO4uXhRiAiENFDfRWh9fx7kj0eMsHZ1cf8qjSMcwbq6uvLy8nCgra0tIy
	 * MDZtLLy+vIkSNSUlL79u0DzqDr37Nnz/Llyw0MDADKQ4cOrV27dseOHWCMX716RYFLacqQkJDFixfPnj
	 * 3bw8MDfr1x44aWlha85MyZM164uLi4xMbGHjx4cOvWrWZmZg1ToncUFZ2pZDRh1PDnUDFBpBIE3LlbXK
	 * 4Zfx2aNgYx3BHrRYvAUcvA4D5H6AV+dAMRuoq+l0MDPRAnE6EszC5YYgskvgVNGYlOfcPz/YoRIqEOQm
	 * q3e/Z0njTp7qpVrleuxERGQnfdMspYOILNzc2nTZt26tQp4FJfXx84vnTpko2NDVALmhj4A1hPnjz5FJ
	 * e3b99u2LBh7969ycnJmzdvNjExYd7+48ePGxkZAaYKCgqlpaUgfOG1CxYsAGQ/fPgABv7ixYtg7JcsWe
	 * Lj45Ofn9+wrwdvrSMlFV+HHy2QJcaJbCAT9cEgHTtWedfbhGNHGwUPZWUgOBurJn3U6RT6cSXa/D+e/Q
	 * 1YilblYtNrhcSOoh9nIrnOyA5bZ5KugwhNI9T9Ec8wE0F4YMige/e7w4fbbtrkoq3t7+z8Mje3mgGqgN
	 * 9LSqAZq3lELUHwjBkzVFRUQP7CG4NCANvp5uYGqJF4gGfRokWRkZG0Dt61a5ctTiS/fPny9evX6etkZ2
	 * dPnz793LlzoEDGjh1LuYPq6uoIHFvs/quqquro6AB/8F7AtKenZ4MfaIODB134OcExDYU4FUNshJCekh
	 * J1M0QaYtwTfubG6MCB86iXIvrjLyTbnacK0hEqo+hcg+bcQdxVaGEfXkQ4g6aWwBUOhqCLZtg8U/nEyV
	 * hsJGC57IdltFWnTuZTptzZvt3x8uW0qKicnJzqSwtTWYUNDbwKRzBQBdKW/hUspYaGBtjRnTt3ggEGo7
	 * t+/fqrV68WFxeDDobPCnKZMr2nT5/WYiwjYGpqCsID5AFY6CFDhoA4efbsmZKS0rx5886fPw8nANzHjh
	 * 2DrwrfCh4SEBvUg9GALwlS5HrfvvGMCQUNgzgBG2NLsFI7d1LTcSsa2uhfl1341NQxfAt//2glpXtDB+
	 * p1QB7VLCvGtKAPutWVlzNSVvXvFfig4md0wp6vLui4eyzGl+r3qO4rFOcZuyNk2r//7XHjLGVlQ0xN44
	 * ODKVenZs8glLUSjmDAEfRuSUkJ9evNmzcBL1A8oCJADIAODg4O3rhxI6AJjIInBxTa4EXgNDU1DfgTIq
	 * Dh/vvvPzDb1GNnYWEBD8D+/ftv3br1+vVr4NjJyQneCJ4E0A+gJeBksMSUM9cwYnQVFd1qG1IS1rFLwN
	 * YJ7pnO2rX0ZARRlMVwCxwdA5cts+jSxZPgEUgSBK9WJbiCQNX+Uml9qYPRaJ8PQm+xaah12CiWT3MSbv
	 * wU/l/uY0Ng2K2b7YwZDgcPul+/nhwaCuavYa6OcAQDlI8ePaLfCZ4haiQmKSnJ0tKSSieIiYm5e/dudH
	 * T0p0+fQA1T5MFpYJLp5ktISKDjxHC1qKioUPwdqEgwvAW4ffBCMMzAN+iQButgqoQ/eKDVvXtibUF7YS
	 * GOxxCDd3hl4UL4diIHMfgYTk6BMjIOHTt6Y/eMFKbSTJeNQPu8sNFOFMbBoKJyidg8A9ORiGf5eanx0t
	 * LGc+dabN0abmwcGxmZm5vbLARXG4P44m2rOcG12ktqXqSe7qMxlMBrb23Z4sEP8TYS4lg8aAdX05g5Ex
	 * 42UYEYpJ2NjcfChQ5dugTjWFlNoysQvgRR/DMh54rFQ1wj2pMyB6n4psRipQFeoHPnzqaDB5vNmOF56l
	 * SQmVmMr+/LL9GMhEWBCRn9K/wsLS2lw71wTP+d/mNdUp26SBl2S5lnUqUUl8bzER4crN2jR2KN6UYNhv
	 * gx1nZq48bRs3NbbfoEdHd6ep4zZ9p36hSCUB5QyOHwKkLlwuLL5Rb+ISbrQxCZvKbgNNIcUKmtkQQRx+
	 * GkcrlU4JIy6v544p1ahw4qW7bQ+a5NQHDjS2BgoJ6e3sOHD5vc0H7x2bu9fbs7dqcb3+4UxBk4aHr555
	 * +DgoJaJ8Rw72/f9p40yZEg4Eu/wwgKy+5nfMXE8sZLrA/gcF7yGoFLjc/F1hiQr6vW356RuALKj3Cudh
	 * hoZQkJg6VLQ7y962/VliYYXL0pU6b069dvy5Ytqqqqrq6uIHqYuf3NATGVNxwaEnKtT58kPLOoSSAGc/
	 * 4Id3+X+/Z152fHfq0cINxffb7T4KJcueI2dqwLNmofhNS7VfDFxENN+ov4Kww/B2mItzpEKpazyTgBMJ
	 * HDiYfK5dZeORxAM5bD4U0ghcbH7c+skTjak8jlPsSjfRYdOtyWkbnv6ysIFV8hPzg5OXnmzJkIlx49eo
	 * wbN05GRgYcQWrvkCaJGJHlZbiWV6Pq9o4doF8fNVpIfDY5OMQG9+Byjx5O5ubUu7TwVAWswT6z+/Bh+s
	 * WL7mPGOOIYQFFlAIFoGL7lvBgF7yBhdI+5hl26hEpKOnzzjZ2YmD1BgAfmilea8sOpfFH8kWcqHpzAP0
	 * 7EnyMJs55CV4JIwdPFeZXLhfoIv8QJIZ3Ro91NTQXvzVAL2wnqoKioSFZWFvHLkCFDFBUVra2tG/8G9Y
	 * 8/hYaG6vTvzwtK1D0lQViIY7Alhv5Us1Mne37EsMUcO+YbxcQkHDvmMWzYPTyA86kRdrfS+lIHHTrE79
	 * 9vct/RMcLExFNPz0VHx1VLy1ld3fncOa/Tp30OHvTYts1t9Wq3+fPtJ0+2GDPGbMQIs2HDTIcOBZ/MdO
	 * BAo379DHv3tuja1fabbxzxLJhgLBKYNQo/CUY//GB16tSzZ8+E6pDRV+nvqANlZWXAt0uXLvLy8lOnTp
	 * 0/f37TmOEP+WTOIzI7jfz0kRn1qIwNy8ryghLQozUFwcxENjAhV7/5xvLatZZv0tjYWEVF10GD3HHfXt
	 * Fodpk2O+TCBXtBBqjz8/OfPn2akpISHx8fFRUVER4eFhISFhAQ4uUV6OISbGXle+OG8+nT1goK5ps3m6
	 * 9ZY7p8ucnChUZz5xrNnKk/bpzxli0NW7Hq68wyovuI69evgyyWk5MbM2bM8OHDF8JXMjISbkNT5mP6OI
	 * a0OkOeX0AeHEfu+41UXUaG2FaD2M/HR6dHD0oNRzchxHgOGVxWh8MxV1NrAd+UKmlpacrKYHe9cJy6vP
	 * HsVsU34Px5pya3X3B/wVS9f//+7du3r1+/Bnfz+fPnVPaiyBDMhPjevXsnTpwwNTWNjo5WUVEZOnTotG
	 * nT7ty5w/xKdXJAq6XCt6TFaXKXNCnbj1RfSdqrka7XSNXl5LaBpNPl6kN069d7NnqIrtbAELXwggFBGJ
	 * 071xwQMy+Vk5Nz7pzbiBHQozymRn2xy1XRaHwrR4zFxX11dDy/pM6qhParFWZgVMBv14C2+pozPWmI4V
	 * mkj8PDw7du3SolJfXnn38aGhoyvaLqX4/G93EseexPHru3dpFP48jSks/m2UqF3DWUjHJjvi4gIODmt9
	 * 8m4ZUiopsWYuyggAg1JogbioofPnxoKoiZV/j48eOtWx5jxzrhR6a0qdhl4FvaoYOXvr4Pv5krmtYG11
	 * VEzAbXDKAy41CRkZHbtm0DjqdPn25iYsL8V+VXpV8Y503K/UDu+YkMc6gejqAKiIrzC6u97631672p1Z
	 * MalDdcvyVO4nAe8vJo0ZUNG/Ly8hoJcbUX2tr6/v23C36fjzhGC9iVN6n2/dSli6upqe/XDQ6KDMH1l4
	 * iIiE2bNg0ePBjssbm5OZ1RVM4fwCNjPcntg8ijU3muW9VbXkEjbq9KKo4is6uMoQQFBel265aEYz0xzS
	 * An4nGUzQEh1X/+efz4MfV5hB3vwCHeMkbXEb5yJVzyATU20bQVrC+XCwdFffs6Ozr6tfJRxtZOcDWrEx
	 * YWtm7duoEDB86cOdPa2ppe3aziUQRP+B6dRr7NqebVVbmCvwm592cy5l61u3Jj40avZlDDn9cBwuN/7g
	 * hdmjgxISFBWCaYwIeHx+ze7dijRwCeWNmY+G794uHDoEHO3t7BIoQvKUIrngQGBq5YsaJfv37z5s0D15
	 * WsKCKVJ5MKI8jc9NrjO/n5pS/w7LowW3L/WDLGrdoJ/j4+N7t0SeIvENjkNZoPMciVC6NG0Yn/QsmJ9P
	 * T006d9+vRxxZPTyOao/IfhnbS0U0DAgxaOZ7cjgqni6uq6e/duPz8/0uoYubUf+ajO/Xs/PgjNlZMv8A
	 * 0mH5iQJ34v1D7zSvXqp/QMkjHhVHvlSmpx7OhmgzgWjy6A8bw4fHhsbKzgfOTm5l654v3LL844TFbRTP
	 * jya97gwdahoWEihy8poqtOFYa7ktv/76PBflNTU2oOSC02+O3bPFW15zIrC+Tmf1g7MvOvP3P3KJXiVD
	 * 36DgW6u9/gcFIbMQFJoEovoTJyJCh7QfSDra3PlCmu2IgXNzO7UF8OH24VEiKqCxSJEsGV5JUWk2dmk0
	 * cnl37IV1VV7du3L6iLe/fuVTmTfxveGRpmz5qe+deMV6fOlOFp/cxLFRUV6Sxb5sfPto5tNkscg7Mxwx
	 * C6PGwYvQ5QrabO2ztkyRLHDh0A+EIqTNZUoYbq6WaV4iH399+to6KiRRRfkbTBFYHmFbL9Kjx1qRb39f
	 * Vdv379jz/+KCMjQ03cr+bVFTg45l2+UoYngFQxdTjS7OPoeFNCIgVnS0Y3pyXm5QBxOPCcaPbv78pfzK
	 * VqVkPMli323boFIfSanwZZ0Tz6gcb3+Z9/WqSmplKtJaLLw4mgijg7nzw0gXyfx/ybl5fX6tWrhw0btn
	 * z5cjiuISl4sFZUDW1SN+zdu3c6MjIBOGEtqlkJxqncqRwObxul775z4OcAUe7akSNOgwb54dUaeHYXB7
	 * bKm0cz0Pg+/fdf4ydPnlRGz0V2dUNRIzjlPrl7GGl9ttaOGDpoUBRDhgxZuXIl3VnX02VTTHu7uOh27Z
	 * raFNM3vlgB4iSe78g15XLdtLRevHhxWcNbSsqNDjU0bZis7mTfxytWmLeNBetFjWDjI+R2Kd7Qcd10ur
	 * i4LF68WFpaGqwyk+OaZob6S0lJie6qVTw1zOE0qxnGFwdBzMnGS0/M7jN+xAhTvJxIRYvgS1//4ZYtVv
	 * T+3aK+BJGoEXxuIXl44ue0yXrjbgsXLgR7DFbZ39+/Hscfft4DM9y5My8oUcd2Eo1nlwqrZSLkLymp2F
	 * N6cK+zPO3dBFm8wtakbdts6LHiNrCClkgR/OoZqTyF1NlBCizanJycFi1aBH7exo0bAwIC6jqttLT0xo
	 * IFoIbTmijzvVpIOAMPpkVyuac7fze6x66uXR/we/MWrrE7d1o3OI+RJbhRgTTez7Qwcv9vpIOaIHE3pn
	 * Wxs7NbsGAB+Hlr164NDAys9SVuNjZ6HTqk1rbTScPnICGe6gXN8IQg9Dt0mNJlRadOvthLo6bulLcAtQ
	 * xlEqGgYE/llrSltQtFiuAEf1JpDOmpK3j8mO4u4c5ZW1vPnTsX9PGGDRvo2cV0AcukM39+II4NN346Pr
	 * V8dCZ20OzExVd1Hv9t51vi4jjEWzmBp6IF2c3v1Mnr1ClXqjWYk+pYgluW4IfYBrsIt/UptX4Fk+M5c+
	 * aAPpaVlX3w4EEVyWFhYSAp+bARecPR1C6LiKDWMA3mcndLDBsoqfbNN8+BJy7P9Ja3gOllsAvPTPi8ef
	 * b+/pVDbm0MX1HTwQVvyJOzSJ1t1cYshC1gioDjWbNmgT3eunUrveML8H3ln38CqLxh4WsEb2CP+whxgN
	 * 1QglAWHygtcVhc/AnFE4HIFrC78HiIidHzNNPGj3cwNfX5nJVa3gY3UhC1WIThQXLPCPLl04aa8gqmbD
	 * AzM5s5c+bgwYOBY2qa4f2AAKOOHRNq2/Wk/lBDBCISETcTiwdtbveR4nvExBKaOzpWw/SWUyIboaz+/T
	 * 3PnnV+V2MgnSX4a5fcDPLAH+RFGTL/ZYOvwbyXxcXFRkZGM2bMAHt89OjR5ORk07Vr46gtQQUQxNGVqc
	 * BEFk4hM+SI/8uZx+W6VGW3BUwvPdL2rlOnQFlZJ0FSiFiCv1KJdidPzyEjnJvwkmCPDQwMZs+ePW3atE
	 * mjR1/Dwa9sbFDrWTGJSmN/jpeA9CCIjdwpXbnGBFH4NcJkJJ4wFzVrloujoy/ZnopIEUzbzlfPPs/LaL
	 * pSVFRkYWGxYMGCfghtw+vHZOKaUINjanTtCabcDyE54pf/46gRRO5Xsru8JIeRI101Nd3pVY1Zgls9xE
	 * 181c/xY/B7QE4AxMMRWoOXCn6CR34T+RxTo2vU2qMniAFDiJME8YyxOnRFy5reN92735OXt6XXgWUJFh
	 * GOmwFlOn6cn5+vOW3aSYTGI/Q9QuuwPc7GauEx3kftIUI3iQ5TiE047Ebyo7zlLawcunV7vGlTcHz8U2
	 * bokCW4vRdqixcvXV0PvBzDcYL4CaGBiNiLZwln8lbQ/34eWtGZcEWohDffnWd6S1tINnwO92b2FD+vuO
	 * UclR75xY6FJbgdFWr4Iy8v79bcuang2xNEBBJXRWgsQoDyXwj1RhcZm0qU1mZ6m3xWBbBbIsYfYPuO0N
	 * +LfgkUE0sYOtRx8uRbs2c7HDsWFRCQkpTEDJ9V2mS8hHgbts0swbWZLny/HYyM9LlieWBp8ZpoK9F0hM
	 * 4itBzxDi4ztqaqYEBcweFUUCukN93soHIO/1ic8FtC/GOP9cwrLGYicM/giZC+pOTNAQOsV6501dLytr
	 * B4kppabfm5CsZC+SzBbV1IYIJ5G08sX+6D0EWO1Bh0HAtgKmh1FVvkHxE6iKPA1VZdICl10URBCTp1OH
	 * 0kcVCT6JGOx6tTcDAklr+rCr1SbxDeasmCy7X89Vfj9esdzp+P9/dPT09vsrWZWYJbvwFm/npc/tgItF
	 * 2Ct/B4lY2o8E4qlzDHIxA6AQ4eP7D1+tffdOXktqxYeaB7j+BGQFzBeDDy+3OuKBIjA7HpfYRT3qLr2P
	 * cqEWcmURNXA/F2NWZ9++r/9pvZ6tVeOjqBrq5tL2TBElx78fEJXbvWtVu3MCrsSlQm5VRUVb3Qk4NC/h
	 * kr5KuSkhHnL6x68waaFJWWIken3r/9pt0YSwzv24FwWEXMccM7rzzBdjdSgDHCKP7+jdSO9bF4rr8VQk
	 * Zdu1r+/rv1zp2Oly5FeXvn5+eXif72uizB1Ut0dPT27Y7du/vg7ppy/Mtrs5E0lM8Qz7cbLyb2f+rqCD
	 * cpgSvy8OjZ+zs/KmNBWHY5nLDpnE26RMd0LF8SkXDpctH8UfF4vFJyEn9l2Gi8iroTQrb9+t2ZOvUuyC
	 * QtrYjQ0HpiGizBIiMbcnNzL1xwHjToHjXvEjtk9SfylkGl9EP37m5ycr2WLkU//CB26hR6/ZoH8es8NG
	 * u2Kr5UhYCRMoxvXj+OxklO/xgchE5BnEhERDUq0b5yzQqo8VxuKt5wJR67gPcRcpWQMOrb13TsWDdFRa
	 * dbt/x8fJgb87AEiwa7Hz9+vHHDfexYZ2yneFDiHMVywewlb1mdXr1e+/j8agVdNeJKSKB793iWuLAQzZ
	 * +v8iWCmeyWdeRaruNMdsGy4RFPM3AjmnrWE7WFWyyXmyQuniImRm3TEo/Xd7MVF7f67juLv/4y2rbNSU
	 * vrUXLy+/fvW3mwGbVPdumNq0ALGhsHTJnigL2gT4wMcUGUaxm1ADVex9d+0qR+J0+iO3fE5s3jmJiIQ9
	 * umphHDfzKvV0WUcfnvKMb1n81dYEXwsuNzcIQhCjXzIiyY5ji8D2ESf6fYJLy/WCAWGyY9e+qPGmW3fX
	 * uAqWm4v3/W8+fVd2XFEbryr7pDersjuJyxRZenZ/Q//zjgW1YofKyA3u4dFOYBhBYhtEdXdxg0qYICsr
	 * VFRUVoz941COXX4cmB6S0nMLtgDceIKapye6TgGAe1S2tzr1xRU2nQe9JTojmJH+IIwKt5G3XrZv3nn9
	 * aKio7q6qlhYS9evKgWb6YWdW55lNupioiKipaVte/SxQ9vzt7gUFchjg1PRmgZDsWSv/zit3//wrl/d9
	 * 0sK71Y5ihBvKgbX5xNwfn4vfiN/8R/DCGIbBxbjhVgB8yWqTTQifxN4MLwnlmuCFlJSd2ZMcNsy5ZQM7
	 * O4yEjeYrisimgxyZuSknLokNPAgZ78EQqh1hlhsmiL0AKE/sarl5QwLvKOK7YcoVv1XxzcRHFxt3nii2
	 * w4nFycZBzbFNvlNmtNwP3DI/xRw/G2cNaSkkZSUmaTJ7seOuR79250YKBw+1CxBAtVcnNzVVXdf/zRHd
	 * +FxoyTgTHaiNBEhM5Qm8RXja+VILQbh1/rjJSJi8f+IbFNW6xTKja9KYho5exW0xvxfKucyBcb0KbWCN
	 * kPHGgxf77l3r3BhoYpSUn17+jNEiyE3S0uLtbV9Zg40R3bkRJhfLVqpz3BI8nArjy+lbXm8XxASBahO7
	 * VGG8TEXvWTvHjoG+kwDieHN+LAiWr1preeCF0sdgTjORxqB6cErKV8EXLr2tV42DDDiRM9lZX9bW2Dgo
	 * Kazzajts0uHl0LXrDAFa8m9YFa1VTgRF4ml8UIXcM5PUuwxanpzzEJ3oKQASPnvVI2dJI0WSM5zYnLfc
	 * JLCwJ2uZGixm49NEcRRAyHkywmlsrhJPIXY/YG29y5s0XfvvZLltifOeNiaFh/OihLMMnc/Cc4OGbpUh
	 * sJifsIvRcyTEZl8NBnOiG0GGdWGjKyduq6zkc8R0mf97TgzHd4UwmJ4Mkd15iJi2cSxCvc/zZ3pOwrAk
	 * 0tEpeEE/qS+WGNYJxAB+1o/r//Gc+aZSUnd9/aOjUxsea0KN4sA7iLAqfRtR2CmZv/xMTEKSo69ejhR6
	 * 0mLaS7Vs6wrCB5t+K5GsfpS30pzwEI3kqg21SgV0Li4fDOR45J9orkcPLwFI9Y0VG9TRKhi8fqAq85Sw
	 * W5eW0aACiDPejf33rePJezZ32MjdPj4goLC6vlaQiCMmobsoH+jhkZGSoqXgMHUnu1NiBMVs7IdjiN0B
	 * SEtuNML8GzJT8QaA9B2EhKlvXoYrKx81jPb77JxNnEsYLN4G+TKNPhuQR+Iig1Fgg0uyBkB17giBFmK1
	 * daKilF29s/zsioxTbXEWxGos9uBT26pqPjN2YMNTLcgFywcoYRvYElrwz4JMLMHKbfdH8HtHa+mIyluH
	 * guDjinYwtE9afxOD8hmqoMHdneyKaGSxLw3aIGcYKxbXbq2tXw119N58/3v3Qp3Nk5JjycuTMxPRZI3/
	 * c2oiIsLf1mzXLFLVPSuEgZXGQpQrMRuklt+yps7dQprmfPWXO7dnWTlEzB2TO+eEa+Hx6qDeWvMpFata
	 * bwkxPi+Le2Zm3bQMfzmyKBv54BdKOmEhImQ4ZYrlvncu5ctLt7bm5uzf3iRZ5gf/8Hq1a5SEiEUO6aML
	 * 5atRqHJe9kLHkzG5TXmzZ1qreRUfDGjRuvHDjwxM7unpqaxZEj5kpKZjt2mG7ebLZ8ucWcOZajRtl8/7
	 * 1tx442YmI2CNngPtQVI/6A389S2pGuzG6Xron8M+vBXbRsPPUh4xjfNw1/QT9Ms9P335tNmmS9bFmwik
	 * qItXVaSoroEVxNBsXFxW3bZte7dxCVyMvlCjU1jXlaDkIqCE3FcdwHgs/WZHiHuT17up886UGpNyUlJR
	 * MTU/ozgy8ClqOoqAh0zosXL56mpER5e/tbWXkZG3vo6blpa7traHifO+euoOC8YoXDjBkWo0cbSUvr9+
	 * un/+23eh07mmIX3htnqYfhzxeKaxiVl4Pdo1otes0nIZ6/Hlx0bbUVSmcQWnEcTgre8y+R//nvIqQ4bl
	 * xISAjlviNRxDcrK0tZ2XXAAD/KWAq5+Q8z5fcDQroIzUBoIbaDzFDaF9jl41vQoUPwpk024eEx1Gf7+P
	 * HTjh07dK5fFz4OWPr+/fuXL18+f/IkIzU1NS4uOTIyITQ01MnJ59Yt51OnLLZvN1m92njxYsO//zaYNs
	 * 1w9GizQYOsv/3Wmcv1wDFqDyzb4acPTvyNwXBXq7T9/mzUCSKRIFot3NQeUHE4pxn6FitJSejN4iMiqM
	 * ATUCECBOP4YOVxTk6OhoaXtLQjDjU0QC0wByDu8SYg8zy2awCiwNEGen1IENwxs2bZ3btXZXOD4uJiOT
	 * k5HR2dZnqMoTVKSkrAqINFf/z4ccT9++4mJg7Xr9traNidPWt57JiFkpKdrKztvHnWY8ZYS0lZ9Otn3r
	 * Oneffu5l27Wnfq5MLh+OGshs8jEYy+m8pKSyGIFMrycbnJYmIJYmIAUDSHU1nrcEObg3jeZ4NPwuWm4E
	 * f05uTJ7o6OoqSD8Q3j78757p2Zme+ECU64kYsbFylLwKkLExFSprZwE4xdXrSB3s1q6FBXdXUXZgI41a
	 * BAsLy8/HXhbXDTFhAtaWlpDx48CAwICPT0DHBx8bWx8b171+v8eaddu2yWLjWbNctw0iTD33678/PPBj
	 * /8YPp//2fbtau7uLgvdj0p79MXe58RmG9qkhJTqyQTRBKHk8DhxNMVbDnYS4KI5fMdRU/dEx5u3tbqHM
	 * 5DrJf0O3UyOXjw1atXNftk1GrZpT8iUGJhcf/ffx04nGBqZFj4hLIKxtzMM5jdjQzJK9DENf475nfu7L
	 * 9rl0NSUlJdORhA8LVr11qzaSgsLATJDh1aZmbmkydPUlJSogMDg83NvbS0HJWVbRQVrXbtMtu40WjFCp
	 * O5c3mTQ4cMsevZ07ZzZxtJSWsOxwon5nniLIgovk/J9DuT+KNxzEqtChAngNmmfk3DL3FASGfiRF+G6R
	 * WBeDATCFvboL//dsL9XkGDNl2rYCQ26OFkyMU4ACD4CAW9RGQRlxs5b56Tq2tQPUP50L/v2bNHW1u7VX
	 * m9jbHlT58+jQFb7ujoa2Hha2zsravrdf26p5qay969lkuWmEyaZDp2rOnIkcbS0kZSUoZ9+ph16mRHEF
	 * 7Y9QzFP6mDcH5eWypODnzIqCl8uCnlEI/zhODx0O3Z0/L4cfAN6vlSrVdFvHnzZtcuSy7Xh85qaET1wB
	 * k5UxFSryp5Ba8PJ0xw0df3/OLSIUDw3r17r169SraD8uHDB+AbTHh8fHxMeHh0SEiUr2+0o2PgrVv2R4
	 * 6Yb91KuZ5G8+cbzpplPGGC+U8/2Xz/vbOEhAseinPD9R5WLGF8rfIIH1iDvZk/P8jP74vPJGqd1vft27
	 * dLlljinoTZgzegwgOviKO8hxjr6wg19z2nd2+v/ftdocPlf8gvEKygoNDGCK6orQhiv0EBgiUC/Zqdnf
	 * 00I+NJQkK8t7fvnTsOGhq258/bnzplfeCA6a5dxsuXG44fbzh4sNl335lyuboDBthcvEhPmRaZvAj6g8
	 * J3XrrUBCfjCrtHNlMS5CF0AUve9bgfo9ktF3hZ6UIuN2DVKpuwsESa3S8uoUcRrKWl1R5sMF5irmrBka
	 * MKYRYaLCkpATf92ePHCRERkf7+wU5OMfw9psrhUl96VFoRwdROO/BlML7pGF+h9l1jSl4TLHnn8tZK/T
	 * zOXCLwIEUReCMTJjhYWQUydvISKN8P7oeSktKVK1fIdlxossvoUlpKVd5u7IJp9DIB8G1FBFNpdfCd5e
	 * SssBwicdyqQvgobyBCK7Bs0BA4H7Jy03d+pOzJyJHON254NmwjIIpgTU1Nki0tUlqXDjYy8sUpSoKnNz
	 * CjvLTk3YcdXCHiFXx287p08VVQsH/27FmDnXog+L///rt8+TLLVrsjOCoq+n//c+SP6FYIkw/5FiE1HG
	 * pYhRP0GhYpi5g/39nFhRkpa8i3AIL37dunoaHBstW+CAYVsWmTHV4oTNjRCjM8630unvXesHhF8ujRDr
	 * dvezDzUBscT2UJbqcEe3iEde4cICR5QQitRWgSXsr3VYMWnc7u39/j+HG3x48fN9UXgcdg//79ampqLF
	 * vtiGAweCtXOuP4l4CRssdY7AK7SngIk/bnBM+HLIIHYPVq24iIaFozNMk4FhB84MABluD2RXBSUtJ339
	 * 2jVzKtt75DSBNL3jU486SeWe914QtnJkyaZGNnF0gn/JeVNdnadUDwwYMHVVVVWbbaEcFXr/rXsY5ORd
	 * VUXXsseWfjWe9lAmuGci63gp8S+fSHH+6pqzu+ffu28ZK3LkEPBF+8eJFlqx0RLCvrzl/jsa49rQLx0N
	 * oUhM4LI3krGNu9v+zVy0de3jE1NbWZ2KUJPnTo0IULF1i22hHBq1a5V11DkqkKwDYfw1HenTiBSVB3Dd
	 * jlR3lLuNyoBQvsfHyCWyaocvjwYZbg9kXw6tUeCL2pQWEBXtt0Bl7b1KehW1mljR3rYWTk22JbnsAbHT
	 * ly5Pz58yxb7YjgzZvdsYvGhM8BofkYX4Mvzlqro774/nsPZWXnFt7jpLy8XFlZ+dy5cyxbokBw/TpSYJ
	 * Wpo+NHJVLi+gChzTjacKahs94/EMT9pUttAwLCmlXy1kPw2bNnWbZaPcE0E6khpK48eWExeWkJeXsP+c
	 * CBLCoQiuOCgoIJE8Lwhn9A7R945bwogfMhmQnEcGbKtGl2lpbMSFmLbpkGBB89elRFRYVlq3UTTHPppU
	 * 8q/kIenkheWESemUvu+43cJU0emUz6GAgOcUlJycqVSgjNxO6abx1JZ/XXEjDYQ4a4aWi405nRZWUVLb
	 * 8jNrzjsWPHzpw5w7IlCioiyILcPog0P0mW4PWNy0rJvCzS15A8OYuU7UeqrySzUukby3wdMwPBwcFh5c
	 * qVW7Zs2bFDoWtXf/5K0bzkYCrjsVrlcnn/5RtdkMj5XG6ylJSbnJxLWlpai8mGCng+qFr1q8H7Hj9+/P
	 * Tp0yxbrZ7gwrfkydmk2oraDFEZ6Xmb3DaQV6M9mBBTWc/UHyIiInbs2DFu3DgQjtS09Tt3gocNc8NzVP
	 * PrV7oIPREXTx869MGiRS43bgS0KLv1Xp8i+NSpUyxbrZ7gpEBSfjgZbFnnfX0UQSqN5hnjENvKc/jmKi
	 * srC6zU+PHjwfRGRkYyXwQsXrzoOmuW54ABIb17h3buHC4hEQNVUjK6W7ewPn1CBwwInTrV/+BB75s3/e
	 * LikoTCq8kg/vSp6EHox6DA0pzciqorK8IHOHHixMmTJ1m2Wj3BICFA8sb51HfOm2zyxEwexOGV8/0LCw
	 * r09PRmzJghIyPj5ORUF3xgkgMDg1xc7pubP7h7NwKqkVGEjc0DD4/78PeW2WKkPrFbUPjq9LnnC5bm7J
	 * TPu3ipOC6W+S1O4sKy1eoJ9r3LQzPCmXdHP36sYCyLWcWBe/eS5+fBmdm8+ZLHjx39/fff1dXVP378KJ
	 * INxn/MPsVGFeye92bRr1mzpr+ztmMSDBICzDDLVusluNJYZqaQrtfI7DRskwoqiovr7NBfpZO7BpOnZn
	 * wqeBscHBwdHU277V98owZM8m4BfMnMZPLyMnL/sNLTMjlrV713vcc8CwQSSGGWrVZKcAVezIzpgNcEi/
	 * lrRkYGb9pZrEeFbL9yo8P8OFdZC4dpm5LgJ7Hkf2N5PkB6wKeHqVkrVxe6uDK/9ZkzZ44dO8ay1eptMP
	 * mFQG9BQcGdO3f+mjmzcvkP40OgJcoS/EW1qcrxI/fuFS9WeGgCmfMQfitKfpi1bOkHH59qBB89elQgW0
	 * 5WptazIH4NHVyjMM2ql5fXsmXLpk+ffuPGjcpp63lZ5N4R5Nl5vLCxSBKMux0HdXLnYDKtckmOslevPg
	 * YHl/IX9qIJVlZW/rI5z39JFrxhEWwVBDOjvPHx8fLy8hMnToSe9OnTp1XOs1fluXSRrqKqHz7k855AXb
	 * nKvzGeWKasUlFROXLkyBcuWFRI3trNcyTY8tUJpnvPnJycs2fPArsbN24MDQ2tRXVAF6w0mtTaKKoEv8
	 * kmlaeQthc+i4raCjTC4cOHv3CpF495o/E2bApb67DBxcXFenp6f//996JFi6ytres7VV+R5wZhESl6BJ
	 * cUk5fXkKrLK3+tI5Zy7ty5Q4cOfeGCjhq8aHpyMIvg1yfY09NzyZIlIHm1tLSYa5rXXsIcSLkfyQd2ou
	 * rJBVvyhJBHfZsMAMEHDhyo71L3rXkXuaPE8vcVCGZGylJSUhQUFEA2gMnJyMig/bn6orbPEng22EoEkw
	 * 9pi3t7D48/1zrXuAYHYNeuXbX/7+P7SmcA9HRBXv1qhC1NQ3CtOGZnZ4OlmTRp0oYNGx7w18qkViz8wu
	 * Xe55Gn55A620U4HFH8gff5gcLzC3leKZWXxyigo27fvl39tW9zSHcd8sgU3gtv7eI5hXXrELY0GcE0vl
	 * lZWdQ4cElJiamp6ezZs+fOnQu3is6TLBVsrUyy9BMvEV5znag2GM2cjwGpNIbcMZiXoGd4kDfGnhpK5m
	 * WCZc3PepL37FFF/ktecmmUO+mkSWpvxpnTQ8lTs+kUERbfZieYDpCBRRk4cKCMjIy7u/vevXunTZt25c
	 * qVBi65ALZH5R/enA7RLczQitdt8ux8UnEUT9zL/VihMJKEenQCeXwSuXdEhfxwcvcwcs8InnC6JkvGev
	 * J8wWoXYUszEUzjq6Oj07FjR4RLr169Dh48yIzyCrGw7pMnH1Kelkb5k/+NJj1uiHazVfvWuenkfRvS+i
	 * ypt7dCayOpI0ve3FpxbQt5dz8v4psUQL5/Xd/L2dLkBNNyFmwtl8tF/NKzZ0/mLlRClXeWNtny+4q1D5
	 * KX5pPZqW24TRMTE6k9U9nylVWEuro6qlomTJjwsuoIquAl39w2a/b0EtlhpMclobWHSBVVVVXQWixbX5
	 * lgUAsUtZKSkj///PPGjRvV1NToxEhh+ttKTPOu6uT8PfnDhjHlsV5tu00vXbq0Z8+eClYqfC2C8/LyVq
	 * 9eDewOHjx49+7dTk5OGRkZxfz0X2GTdD9vzWlulr1sUe7mDS/2H3irrV36tedZNF/R1tamRzRYjr8Cwe
	 * 7u7kCwpqZmVlYWM92M2p2mwe9U/v7dp7S0Qh//vMuaeRcvfkpta1KYhvX27dvM3DQW4pYgmNnKYIPrnw
	 * L0xVvy5RM+faooKWmTDQoP+b59+/7555/4+PjS0lKWsJYgmAIOmv7du3dM+OAGUDY4MzMzPT39zZs3KS
	 * kpJQzymCfTFpr6I7wkKCiIOpm+jrCgi1x5+/atvb393Llzu3XrBlI4OJhN3Gl+ginsnj17Bh3fli1bwA
	 * upuamEgYHBuXPngEhFRUXgmAkf/EtXV5f+lT4AC3T37t2ioqJ21ZrQdNBEm3BZtWrV0aNHqRZghUQzEk
	 * z1dM7OzlRyGTS9khIvZ+r9+/cmJiaJiYkUpufPnweLAv9iEgxiY8yYMVJSUmBrCwsLqVAxWN9Hjx6BOQ
	 * ezTeJ5coCyr68v5Qu2+XsJ1P72228LFiwYOXIk0MwS1nIEUyFMc3Pz3bt3JycnX7hwAfy57du3p6WlwX
	 * /V1dVDQkKOHDlCDSZTIDo6Oq5fv37dunURERFRUVFww+DnyZMn4fzU1FQw52FhYYcPH96/fz88AxT65W
	 * 09GeDixYtUFLJv376enp4sYS1EcEBAwMSJExcvXgwaDtrd1tZ2586dYFZVcXF1ddXU1Lx//z5NMMlfpB
	 * FOBvtKTWwEWztq1Cg4hn/5+/uDQwOiUEZGBl5e0kb9tprFycmpd+/eHA5HWlqa6sHY0hIEA4jLly83Nj
	 * besWOHjo7OtWvXqD39AD6wqVZWViAwQEWAQaUJBts8efLkM7j88ccfJG9TziiwPXp6eiRvfzgPBQWFly
	 * 9fAt9gm1VUVLKzs9uDDQaxNHXqVGgHaJycnByWsBYi2Nraes2aNZRsBRdETk4OLCgwCsL31q1bdnZ2wL
	 * Gfnx8Y5ry8POqVAPro0aNPnToF/4K7dezYMRAe8vLyIELA9oDB3rBhA3g2L168gAdj5syZbm5uZNVVK9
	 * tqmT17NhDMLv7XQgRTca6goCAwk/n5vLTrq1ev6uvr37hxY8WKFQcPHnz9+jVIAlAIcXFxYImpqfOgCk
	 * DmgqtHXcXBwWHSpEnU9ie6urrw95SUFEA/Ojoa+N68eTOIECqhorwdJMXCww8qAhqQxaslCKajYMAWM1
	 * AAZKenp3/48IE6pv5L8wemtFqk7NWrV/RfQEBTZ8ILnz179ujRo3L+0qui21LMiGH9ERWQZL169QKfmH
	 * YYaj2fDbE1GcHMphSkWaudL+zLRZfdahzXdT6IqP/++6+aCC6vuk4Xy3ET22C21F9AO4WEhFAzsWNiYu
	 * rfVhEkFqXHoMCBl5dXzeViocuC69AbJrA0swQ3O8Ggbq2srIBFcFV9fX1BKdHqiE7cA3aBQip0SOEI+A
	 * 4fPvzOnTskHgCiHFl4FTwDNjY2b968gb/AdRqcdc0WlmBBS1hY2K5duzZs2GBoaAj6HmTu/v37qeVdvL
	 * 29g4ODbW1tT58+nZmZCS4svYmdgYHBmjVr1NXV4RmA04yMjAIDA42NjeFqcJyRkQEOH7jL7u7u5ezET5
	 * bg5tbB58+f/+uvv6iIuL29/c6dO5csWZKbmwuu2+XLlw8cOLB8+XIzM7MzZ85QsiEtLY0aoVRRUYmOjg
	 * ZPV0FBYcaMGYA7EAyn+fv7b9++XUdHhxqwZAtLcPMWJyenEydOgEgAFaGmpgZorl27NigoKD09fceOHU
	 * Cwtrb2nDlzwEhT54OF/uOPP/bu3TthwgQwzCSe+TJ06NDXr1/HxsaCCQf9kJycfPXqVXgAXrTdlH+W4N
	 * ZSHBwcjh8/DgQDu4cPHwag161bd+8ebwH3FStWHD16NDQ0VFpa2sPDg/LhDh06dOnSJTDSYJiB3Zs3b2
	 * poaIAhB2kREBCgrKwMBIOdhmtu2rQJjtkWZglu3vLw4UMgDwguKChwdHTU19cHLQGAUh5bRETE+/fvwd
	 * uj/gI21dXVlR7CBKzv3r0LmFLRifj4eNAVWVlZADcY4KioKLZ5WYJbSA3XjHnV/EvD3DI2msYSzBaWYL
	 * awhSWYLWxhCWYLW1iC2cISzBa2sASzhS0swWxhC0swW1iC2cIWlmC2sIUlmC1socv/A7l1Ehi5ONHnAA
	 * AAAElFTkSuQmCC"/>
	 */
	public NavXMXP() {
		try {
			ahrs = new AHRS(SPI.Port.kMXP);
		} catch (RuntimeException ex) {
			DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
		}
	}

	/**
	 * positive means rotated clockwise.
	 * 
	 * @return The current yaw(rotation in z-axis) value in degrees (-180 to
	 *         180).
	 * @see {@link AHRS#getYaw()}
	 */
	public double getYaw() {
		return ahrs.getYaw();
	}

	/**
	 * positive angle means tilted backwards.
	 * 
	 * @return The current pitch(rotation in x-axis) value in degrees (-180 to
	 *         180).
	 * @see {@link AHRS#getPitch()}
	 */
	public double getPitch() {
		return ahrs.getPitch();
	}

	/**
	 * positive means rolled left.
	 * 
	 * @return The current roll(rotation in y-axis) value in degrees (-180 to
	 *         180).
	 * @see {@link AHRS#getRoll()}
	 */
	public double getRoll() {
		return ahrs.getRoll();
	}

	/**
	 * next call to {@link #getYaw()} will be relative to the current yaw value.
	 * 
	 * @see {@link AHRS#zeroYaw()}
	 */
	public void resetYaw() {
		ahrs.zeroYaw();
	}

}
